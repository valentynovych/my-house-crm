package com.example.myhouse24user.service;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.model.apartments.ApartmentShortResponse;
import org.springframework.data.domain.Page;

public interface ApartmentService {
    Page<ApartmentShortResponse> getOwnerApartments(String name, int page, int pageSize);

    Apartment findApartmentByIdAndOwner(Long apartmentId, String name);
}
