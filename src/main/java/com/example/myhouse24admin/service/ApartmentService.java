package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.model.apartments.ApartmentExtendResponse;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.model.meterReadings.ApartmentNumberResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ApartmentService {
    void addNewApartment(ApartmentAddRequest apartmentAddRequest);

    Page<ApartmentResponse> getApartments(int page, int pageSize, Map<String, String> searchParams);

    ApartmentExtendResponse getApartmentById(Long apartmentId);

    void updateApartment(Long apartmentId, ApartmentAddRequest apartmentRequest);
    Page<ApartmentNumberResponse> getApartmentsForSelect(SelectSearchRequest selectSearchRequest,
                                                         Long houseId, Long sectionId);
}
