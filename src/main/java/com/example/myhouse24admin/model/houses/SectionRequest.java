package com.example.myhouse24admin.model.houses;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record SectionRequest(Long id,
                             @NotEmpty(message = "{validation-not-empty}")
                             @Size(max = 100, message = "{validation-size-max}")
                             String name,
                             boolean deleted,
                             @NotEmpty(message = "{validation-not-empty}")
                             @Size(max = 10, message = "{validation-invalid-value}")
                             String rangeApartmentNumbers) {
}
