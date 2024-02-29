package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.apartmentOwner.*;
import com.example.myhouse24admin.model.invoices.OwnerNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ApartmentOwnerService {
    void createApartmentOwner(CreateApartmentOwnerRequest createApartmentOwnerRequest, MultipartFile avatar);
    ApartmentOwnerResponse getApartmentOwnerResponse(Long id);
    void updateApartmentOwner(EditApartmentOwnerRequest editApartmentOwnerRequest, Long id, MultipartFile multipartFile);
    Page<TableApartmentOwnerResponse> getApartmentOwnerResponsesForTable(int page, int pageSize, FilterRequest filterRequest);
    void deleteOwnerById(Long id);
    ViewApartmentOwnerResponse getApartmentOwnerResponseForView(Long id);

    Page<ApartmentOwnerShortResponse> getShortResponseOwners(int page, int pageSize, String fullName);
    Page<OwnerNameResponse> getOwnerNameResponses(SelectSearchRequest selectSearchRequest);
}
