package com.example.myhouse24admin.service;

import com.example.myhouse24admin.entity.CashSheetType;

import java.util.List;

public interface CashRegisterService {
    List<CashSheetType> getSheetStatuses();
}
