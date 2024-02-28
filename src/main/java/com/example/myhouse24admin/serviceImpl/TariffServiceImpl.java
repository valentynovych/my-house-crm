package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Tariff;
import com.example.myhouse24admin.entity.TariffItem;
import com.example.myhouse24admin.mapper.TariffMapper;
import com.example.myhouse24admin.model.invoices.TariffItemResponse;
import com.example.myhouse24admin.model.invoices.TariffNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.model.tariffs.TariffRequest;
import com.example.myhouse24admin.model.tariffs.TariffRequestWrap;
import com.example.myhouse24admin.model.tariffs.TariffResponse;
import com.example.myhouse24admin.repository.TariffItemRepo;
import com.example.myhouse24admin.repository.TariffRepo;
import com.example.myhouse24admin.service.TariffService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.myhouse24admin.specification.TariffItemSpecification.byTariffId;
import static com.example.myhouse24admin.specification.TariffSpecification.byDeleted;
import static com.example.myhouse24admin.specification.TariffSpecification.byTariffName;

@Service
public class TariffServiceImpl implements TariffService {

    private final TariffRepo tariffRepo;
    private final TariffItemRepo tariffItemRepo;
    private final TariffMapper mapper;
    private final Logger logger = LogManager.getLogger(TariffServiceImpl.class);

    public TariffServiceImpl(TariffRepo tariffRepo, TariffItemRepo tariffItemRepo, TariffMapper mapper) {
        this.tariffRepo = tariffRepo;
        this.tariffItemRepo = tariffItemRepo;
        this.mapper = mapper;
    }

    @Override
    public void addNewTariff(TariffRequestWrap tariffRequestWrap) {
        logger.info("addNewTariff() -> start");
        TariffRequest tariffRequest = tariffRequestWrap.getTariffRequest();
        Tariff tariff = mapper.tariffRequestToTariff(tariffRequest);
        logger.info("addNewTariff() -> forEach -> set to tariffItems current tariff");
        tariff.getTariffItems().forEach(tariffItem -> tariffItem.setTariff(tariff));
        Tariff save = tariffRepo.save(tariff);
        logger.info("addNewTariff() -> exit, success save new tariff with id: {}", save.getId());
    }

    @Override
    public Page<TariffResponse> getAllTariffs(int page, int pageSize) {
        logger.info("getAllTariffs() -> start with parameters - page: {}, pageSize: {}", page, pageSize);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "name"));
        Page<Tariff> tariffPage = tariffRepo.findAllByDeletedIsFalse(pageable);
        List<TariffResponse> tariffResponseList = mapper.tariffListToTariffResponseList(tariffPage.getContent());
        Page<TariffResponse> tariffResponsePage =
                new PageImpl<>(tariffResponseList, pageable, tariffPage.getTotalElements());
        logger.info("getAllTariffs() -> exit, return element on page: {}", tariffResponsePage.getNumber());
        return tariffResponsePage;
    }

    @Override
    public TariffResponse getTariffById(Long tariffId) {
        logger.info("getTariffById() -> start");
        Optional<Tariff> byId = tariffRepo.findById(tariffId);
        Tariff tariff = byId.orElseThrow(() ->
                new EntityNotFoundException(String.format("Tariff with id: %s not found", tariffId)));
        TariffResponse tariffResponse = mapper.tariffToTariffResponse(tariff);
        logger.info("getTariffById() -> exit, return TariffResponse");
        return tariffResponse;
    }

    @Override
    public void editTariff(Long tariffId, TariffRequestWrap tariffRequest) {
        logger.info("editTariff() -> start");

        if (tariffRequest.getTariffItemToDelete() != null
                && !tariffRequest.getTariffItemToDelete().isEmpty()) {
            List<Long> tariffItemToDelete = tariffRequest.getTariffItemToDelete();
            tariffItemRepo.deleteAllById(tariffItemToDelete);
        }

        Optional<Tariff> byId = tariffRepo.findById(tariffId);
        Tariff tariff = byId.orElseThrow(() ->
                new EntityNotFoundException(String.format("Tariff with id: %s not found", tariffId)));
        mapper.updateTariffFromTariffRequest(tariff, tariffRequest.getTariffRequest());

        tariff.getTariffItems().forEach(tariffItem -> tariffItem.setTariff(tariff));
        tariffRepo.save(tariff);
        logger.info("editTariff() -> exit");
    }

    @Override
    public boolean deleteTariffById(Long tariffId) {
        logger.info("deleteTariffById() -> start with id: {}", tariffId);
        Optional<Tariff> byId = tariffRepo.findById(tariffId);
        if (byId.isPresent()) {
            Tariff tariff = byId.get();
            tariff.setDeleted(true);
            tariffRepo.save(tariff);
            //TODO add check using tariff in invoices
            logger.info("deleteTariffById() -> tariff with id: {} mark isDeleted and save", tariffId);
            return true;
        }
        logger.error("deleteTariffById() -> Tariff with id: {} not found", tariffId);
        return false;
    }

    @Override
    public Page<TariffNameResponse> getTariffsForSelect(SelectSearchRequest selectSearchRequest) {
        logger.info("getTariffsForSelect - Getting tariff name responses for select " + selectSearchRequest.toString());
        Pageable pageable = PageRequest.of(selectSearchRequest.page()-1, 10);
        Page<Tariff> tariffs = getFilteredTariffsForSelect(selectSearchRequest, pageable);
        List<TariffNameResponse> tariffNameResponses = mapper.tariffListToTariffNameResponseList(tariffs.getContent());
        Page<TariffNameResponse> tariffNameResponsePage = new PageImpl<>(tariffNameResponses, pageable, tariffs.getTotalElements());
        logger.info("getTariffsForSelect - Tariff name responses were got");
        return tariffNameResponsePage;
    }

    private Page<Tariff> getFilteredTariffsForSelect(SelectSearchRequest selectSearchRequest, Pageable pageable) {
        Specification<Tariff> tariffSpecification = Specification.where(byDeleted());
        if(!selectSearchRequest.search().isEmpty()){
            tariffSpecification = tariffSpecification.and(byTariffName(selectSearchRequest.search()));
        }
        return tariffRepo.findAll(tariffSpecification, pageable);
    }

    @Override
    public List<TariffItemResponse> getTariffItems(Long tariffId) {
        logger.info("getTariffItems - Getting tariff item responses by tariff id "+tariffId);
        List<TariffItem> tariffItems = tariffItemRepo.findAll(byTariffId(tariffId));
        List<TariffItemResponse> tariffItemResponses = mapper.tariffItemListToTariffItemResponse(tariffItems);
        logger.info("getTariffItems - Tariff item responses were got");
        return tariffItemResponses;
    }
}
