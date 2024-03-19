package com.example.myhouse24user.service;

import com.example.myhouse24user.model.tariff.TariffResponse;

public interface TariffService {
    TariffResponse getApartmentTariff(Long apartmentId);
}
