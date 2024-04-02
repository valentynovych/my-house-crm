package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.CashSheet;
import com.example.myhouse24admin.entity.Invoice;
import com.example.myhouse24admin.mapper.CashSheetMapper;
import com.example.myhouse24admin.model.cashRegister.*;
import com.example.myhouse24admin.repository.CashSheetRepo;
import com.example.myhouse24admin.service.CashRegisterService;
import com.example.myhouse24admin.specification.CashSheetSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public CashSheetResponse getSheetById(Long sheetId) {
        logger.info("getSheetById() -> start, with id: {}", sheetId);
        CashSheet cashSheetById = findCashSheetById(sheetId);
        CashSheetResponse sheetResponse = cashSheetMapper.cashSheetToCashSheetWithOwnerResponse(cashSheetById);
        logger.info("getSheetById() -> end, return CashSheetResponse");
        return sheetResponse;
    }

    @Override
    public void updateSheetById(Long sheetId, CashSheetIncomeUpdateRequest updateRequest) {
        logger.info("updateSheetById() -> start, with id: {}", sheetId);
        CashSheet cashSheetById = findCashSheetById(sheetId);
        cashSheetMapper.updateCashSheetFromCashSheetIncomeUpdateRequest(cashSheetById, updateRequest);
        if (cashSheetById.getInvoice() != null) {
            cashSheetById.getInvoice().setProcessed(updateRequest.isProcessed());
        }
        CashSheet save = cashSheetRepo.save(cashSheetById);
        logger.info("updateSheetById() -> end, success update CashSheet with id: {}", save.getId());
    }

    @Override
    public void addNewExpenseSheet(CashSheetExpenseAddRequest addRequest) {
        logger.info("addNewExpenseSheet() -> start");
        CashSheet cashSheet = cashSheetMapper.cashSheetExpenseAddRequestToCashSheet(addRequest);
        CashSheet save = cashSheetRepo.save(cashSheet);
        logger.info("addNewExpenseSheet() -> end, save new Expense Sheet with id: {}", save.getId());
    }

    @Override
    public void updateSheetById(Long sheetId, CashSheetExpenseUpdateRequest updateRequest) {
        logger.info("updateSheetById() -> start, with id: {}", sheetId);
        CashSheet cashSheetById = findCashSheetById(sheetId);
        cashSheetMapper.updateCashSheetFromCashSheetExpenseUpdateRequest(cashSheetById, updateRequest);
        CashSheet save = cashSheetRepo.save(cashSheetById);
        logger.info("updateSheetById() -> end, success update CashSheet with id: {}", save.getId());
    }

    @Override
    public String deleteCashSheetById(Long sheetId) {
        logger.info("deleteCashSheetById() -> start, with id: {}", sheetId);
        CashSheet cashSheet = findCashSheetById(sheetId);
        if (cashSheet.isProcessed()) {
            return "Error";
        }
        cashSheet.setDeleted(true);
        cashSheetRepo.save(cashSheet);
        logger.info("deleteCashSheetById() -> end, success mark CashSheet with id: {} as isDeleted ", sheetId);
        return "Success";
    }

    @Override
    public void saveCashSheet(CashSheet cashSheet) {
        cashSheetRepo.save(cashSheet);
    }

    @Override
    public void updateCashSheetFromInvoice(Invoice invoice) {
        logger.info("updateCashSheetFromInvoice() -> start, with Invoice id: {}", invoice.getId());
        CashSheet cashSheet = cashSheetRepo.findCashSheetByInvoice_Id(invoice.getId()).orElseThrow(() -> {
            logger.error("updateCashSheetFromInvoice() -> CashSheet with Invoice id: {} not found", invoice.getId());
            return new EntityNotFoundException(String.format("CashSheet with Invoice id: %s not found", invoice.getId()));
        });
        cashSheetMapper.updateCashSheetFromInvoice(cashSheet, invoice);
        saveCashSheet(cashSheet);
        logger.info("updateCashSheetFromInvoice() -> end, success update CashSheet with Invoice id: {}", invoice.getId());
    }

    private CashSheet findCashSheetById(Long cashSheetId) {
        logger.info("findCashSheetById() -> start, with id: {}", cashSheetId);
        Optional<CashSheet> byId = cashSheetRepo.findCashSheetByIdAndDeletedIsFalse(cashSheetId);
        CashSheet cashSheet = byId.orElseThrow(() -> {
            logger.error("findCashSheetById() -> CashSheet with id: {} not found", cashSheetId);
            return new EntityNotFoundException(String.format("CashSheet with id: %s not found", cashSheetId));
        });
        logger.info("findCashSheetById() -> CashSheet isPresent, return CashSheet");
        return cashSheet;
    }

    private Page<CashSheet> findCashSheetsBySearchParams(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("findCashSheetsBySearchParams() -> start, with parameters: {}", searchParams);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "creationDate"));
        CashSheetSpecification specification = new CashSheetSpecification(searchParams);
        Page<CashSheet> all = cashSheetRepo.findAll(specification, pageable);
        logger.info("findCashSheetsBySearchParams() -> end, with result elements: {}", all.getNumberOfElements());
        return all;
    }
}
