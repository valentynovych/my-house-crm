package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.CashSheet;
import com.example.myhouse24admin.mapper.CashSheetMapper;
import com.example.myhouse24admin.model.cashRegister.CashSheetTableResponse;
import com.example.myhouse24admin.repository.CashSheetRepo;
import com.example.myhouse24admin.service.CashRegisterService;
import com.example.myhouse24admin.specification.CashSheetSpecification;
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
        Page<CashSheet> cashSheetPage = findCashSheetsBySearchParams(page, pageSize, searchParams);
        List<CashSheetTableResponse> responseList =
                cashSheetMapper.sashSheetListToCashSheetTableResponseList(cashSheetPage.getContent());
        Page<CashSheetTableResponse> responsePage = new PageImpl<>(responseList,
                cashSheetPage.getPageable(), cashSheetPage.getTotalElements());
        return responsePage;
    }

    private Page<CashSheet> findCashSheetsBySearchParams(int page, int pageSize, Map<String, String> searchParams) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "creationDate"));
        CashSheetSpecification specification = new CashSheetSpecification(searchParams);
        Page<CashSheet> all = cashSheetRepo.findAll(specification, pageable);
        return all;
    }
}
