package com.example.myhouse24admin.model.tariffs;

import java.time.Instant;
import java.util.List;

public record TariffResponse(
        Long id,
        String name,
        String description,
        Instant lastModify,
        List<TariffItemResponse> tariffItems) {
}
