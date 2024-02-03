package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.tariffs.TariffRequestWrap;
import com.example.myhouse24admin.model.tariffs.TariffResponse;
import org.springframework.data.domain.Page;

public interface TariffService {
    void addNewTariff(TariffRequestWrap tariffRequest);

    Page<TariffResponse> getAllTariffs(int page, int pageSize);

    TariffResponse getTariffById(Long tariffId);

    void editTariff(Long tariffId, TariffRequestWrap tariffRequest);

    boolean deleteTariffById(Long tariffId);
}
