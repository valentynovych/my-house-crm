package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.CashSheetType;
import com.example.myhouse24admin.service.CashRegisterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CashRegisterServiceImpl implements CashRegisterService {

    private final Logger logger = LogManager.getLogger(CashRegisterServiceImpl.class);

    @Override
    public List<CashSheetType> getSheetStatuses() {
        return Arrays.stream(CashSheetType.values()).toList();
    }
}
