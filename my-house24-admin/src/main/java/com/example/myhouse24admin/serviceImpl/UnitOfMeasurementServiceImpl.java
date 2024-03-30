package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.UnitOfMeasurement;
import com.example.myhouse24admin.exception.ServiceAlreadyUsedException;
import com.example.myhouse24admin.mapper.UnitOfMeasurementMapper;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDto;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDtoListWrap;
import com.example.myhouse24admin.repository.InvoiceItemRepo;
import com.example.myhouse24admin.repository.UnitOfMeasurementRepo;
import com.example.myhouse24admin.service.UnitOfMeasurementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnitOfMeasurementServiceImpl implements UnitOfMeasurementService {

    private final UnitOfMeasurementRepo unitOfMeasurementRepo;
    private final InvoiceItemRepo invoiceItemRepo;
    private final UnitOfMeasurementMapper mapper;
    private final Logger logger = LogManager.getLogger(UnitOfMeasurementServiceImpl.class);

    public UnitOfMeasurementServiceImpl(UnitOfMeasurementRepo unitOfMeasurementRepo, InvoiceItemRepo invoiceItemRepo, UnitOfMeasurementMapper mapper) {
        this.unitOfMeasurementRepo = unitOfMeasurementRepo;
        this.invoiceItemRepo = invoiceItemRepo;
        this.mapper = mapper;
    }

    @Override
    public List<UnitOfMeasurementDto> getAllMeasurementUnits() {
        logger.info("getAllMeasurementUnits() -> start");
        List<UnitOfMeasurement> all = unitOfMeasurementRepo.findAll(
                Specification.where((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("deleted"), false)));
        List<UnitOfMeasurementDto> dtoList = mapper.unitOfMeasurementListToUnitOfMeasurementDtoList(all);
        logger.info("getAllMeasurementUnits() -> exit, return list size: {}", dtoList.size());
        return dtoList;
    }

    @Override
    public void updateMeasurementUnist(UnitOfMeasurementDtoListWrap measurementListWrap) {
        logger.info("updateMeasurementUnist() -> start");
        List<UnitOfMeasurement> unitOfMeasurements =
                mapper.unitOfMeasurementListDtoToUnitOfMeasurementList(measurementListWrap.getUnitOfMeasurements());
        unitOfMeasurementRepo.saveAll(unitOfMeasurements);
        logger.info("updateMeasurementUnist() -> success update unitOfMeasurements");

        if (measurementListWrap.getUnitsToDelete() != null
                && !measurementListWrap.getUnitsToDelete().isEmpty()) {
            List<Long> unitsToDelete = measurementListWrap.getUnitsToDelete();
            checkUsedMeasurementUnistInInvoices(unitsToDelete);

            logger.info("updateMeasurementUnist() -> To have unitsToDelete: " + unitsToDelete);
            List<UnitOfMeasurement> allById = unitOfMeasurementRepo.findAllById(unitsToDelete);
            allById.forEach(unitOfMeasurement -> unitOfMeasurement.setDeleted(true));
            unitOfMeasurementRepo.deleteAll(allById);
            logger.info("updateMeasurementUnist() -> Success deleting MeasurementUnist ");
        }
    }

    private void checkUsedMeasurementUnistInInvoices(List<Long> unitIds) {
        List<Long> usedUnitIds = new ArrayList<>();
        for (Long unitId : unitIds) {
            if (invoiceItemRepo.existsInvoiceItemByService_UnitOfMeasurement_Id(unitId)) {
                usedUnitIds.add(unitId);
            }
        }

        if (!usedUnitIds.isEmpty()) {
            String unitNamesByIds = getUnitNamesByIds(usedUnitIds);
            throw new ServiceAlreadyUsedException("MeasurementUnist with ids: [%s] used in Invoices, can`t delete them",
                    unitNamesByIds);
        }
    }

    private String getUnitNamesByIds(List<Long> unitIds) {
        List<UnitOfMeasurement> allById = unitOfMeasurementRepo.findAllById(unitIds);
        return allById.stream()
                .map(UnitOfMeasurement::getName)
                .collect(Collectors.joining(", "));
    }
}
