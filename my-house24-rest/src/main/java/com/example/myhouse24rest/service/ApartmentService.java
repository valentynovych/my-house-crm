package com.example.myhouse24rest.service;

import com.example.myhouse24rest.model.apartment.ApartmentShortResponse;
import org.springframework.data.domain.Page;

import java.security.Principal;

public interface ApartmentService {
    Page<ApartmentShortResponse> getAllApartments(int page, int pageSize, Principal principal);
}
