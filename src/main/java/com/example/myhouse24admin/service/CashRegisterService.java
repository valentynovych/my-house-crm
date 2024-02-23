package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.cashRegister.CashSheetTableResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface CashRegisterService {

    Page<CashSheetTableResponse> getSheets(int page, int pageSize, Map<String, String> searchParams);
}