package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.CashSheet;
import com.example.myhouse24admin.mapper.CashSheetMapper;
import com.example.myhouse24admin.model.cashRegister.CashSheetIncomeAddRequest;
import com.example.myhouse24admin.model.cashRegister.CashSheetTableResponse;
import com.example.myhouse24admin.repository.CashSheetRepo;
import com.example.myhouse24admin.service.CashRegisterService;
import com.example.myhouse24admin.specification.CashSheetSpecification;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CashRegisterServiceImpl implements CashRegisterService {

    private final CashSheetRepo cashSheetRepo;
    private final CashSheetMapper cashSheetMapper;
    private final Logger logger = LogManager.getLogger(CashRegisterServiceImpl.class);

    public CashRegisterServiceImpl(CashSheetRepo cashSheetRepo, CashSheetMapper cashSheetMapper) {
        this.cashSheetRepo = cashSheetRepo;
        this.cashSheetMapper = cashSheetMapper;
    }

    @Override
    public Page<CashSheetTableResponse> getSheets(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("getSheets() -> start, with parameters: {}", searchParams);
        Page<CashSheet> cashSheetPage = findCashSheetsBySearchParams(page, pageSize, searchParams);
        List<CashSheetTableResponse> responseList =
                cashSheetMapper.sashSheetListToCashSheetTableResponseList(cashSheetPage.getContent());
        Page<CashSheetTableResponse> responsePage = new PageImpl<>(responseList,
                cashSheetPage.getPageable(), cashSheetPage.getTotalElements());
        logger.info("getSheets() -> end, with result elements: {}", responsePage.getNumberOfElements());
        return responsePage;
    }

    @Override
    public void addNewIncomeSheet(CashSheetIncomeAddRequest addRequest) {
        logger.info("addNewIncomeSheet() -> start");
        CashSheet cashSheet = cashSheetMapper.cashSheetIncomeAddRequestToCashSheet(addRequest);
        CashSheet save = cashSheetRepo.save(cashSheet);
        logger.info("addNewIncomeSheet() -> end, save new Income Sheet with id: {}", save.getId());
    }

    @Override
    public String getNextSheetNumber() {
        logger.info("getNextSheetNumber() -> start");
        Long maxId = cashSheetRepo.getMaxId();
        String nextSheetNumber = StringUtils.leftPad(String.valueOf(maxId), 10, "0000000000");
        logger.info("getNextSheetNumber() -> end, with result: {}", nextSheetNumber);
        return nextSheetNumber;
    }

    private Page<CashSheet> findCashSheetsBySearchParams(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("findCashSheetsBySearchParams() -> start, with parameters: {}", searchParams);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "creationDate"));
        CashSheetSpecification specification = new CashSheetSpecification(searchParams);
        Page<CashSheet> all = cashSheetRepo.findAll(specification, pageable);
        logger.info("findCashSheetsBySearchParams() -> end, with result elements: {}", all.getNumberOfElements());
        return all;
    }
}
