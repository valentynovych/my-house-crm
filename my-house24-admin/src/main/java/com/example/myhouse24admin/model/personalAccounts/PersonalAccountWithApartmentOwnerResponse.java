package com.example.myhouse24admin.model.personalAccounts;

import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerShortResponse;

public record PersonalAccountWithApartmentOwnerResponse(Long id,
                                                        String accountNumber,
                                                        ApartmentOwnerShortResponse apartmentOwner) {
}
