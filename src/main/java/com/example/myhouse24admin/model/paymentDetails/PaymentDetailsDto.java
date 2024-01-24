package com.example.myhouse24admin.model.paymentDetails;

import jakarta.validation.constraints.Size;

public record PaymentDetailsDto(Long id,
                                @Size(min = 2, max = 150,
                                        message = "{validation-size-min-max}") String companyName,
                                @Size(min = 2, max = 350,
                                        message = "{validation-size-min-max}") String companyDetails) {
}
