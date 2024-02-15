package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.model.apartments.ApartmentExtendResponse;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ApartmentService {
    void addNewApartment(ApartmentAddRequest apartmentAddRequest);

    Page<ApartmentResponse> getApartments(int page, int pageSize, Map<String, String> searchParams);

    ApartmentExtendResponse getApartmentById(Long apartmentId);

    void updateApartment(Long apartmentId, ApartmentAddRequest apartmentRequest);
}
