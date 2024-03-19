package com.example.myhouse24user.model.tariff;

import java.util.List;

public record TariffResponse(Long id, String name, List<TariffItemResponse> tariffItems) {
}
