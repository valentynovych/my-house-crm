package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.invoices.TariffItemResponse;
import com.example.myhouse24admin.model.invoices.TariffNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.model.tariffs.TariffRequestWrap;
import com.example.myhouse24admin.model.tariffs.TariffResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TariffService {
    void addNewTariff(TariffRequestWrap tariffRequest);

    Page<TariffResponse> getAllTariffs(int page, int pageSize);

    TariffResponse getTariffById(Long tariffId);

    void editTariff(Long tariffId, TariffRequestWrap tariffRequest);

    boolean deleteTariffById(Long tariffId);
    Page<TariffNameResponse> getTariffsForSelect(SelectSearchRequest selectSearchRequest);
    List<TariffItemResponse> getTariffItems(Long tariffId);
}
