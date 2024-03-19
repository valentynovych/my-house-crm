package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.MasterRequest;
import com.example.myhouse24user.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24user.model.masterRequest.MasterRequestTableResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MasterRequestMapper {
    List<MasterRequestTableResponse> masterRequestListToMasterRequestTableResponseList(List<MasterRequest> masterRequests);

    MasterRequestTableResponse masterRequestToMasterRequestTableResponse(MasterRequest masterRequest);

    @Mapping(target = "apartment", source = "apartment")
    @Mapping(target = "creationDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "apartmentOwnerPhone", source = "apartment.owner.phoneNumber")
    @Mapping(target = "status", constant = "NEW")
    MasterRequest masterRequestAddRequestToMasterRequest(MasterRequestAddRequest masterRequest, Apartment apartment);

    @Named(value = "apartmentIdToApartment")
    static Apartment apartmentIdToApartment(Long id) {
        if (id == null) {
            return null;
        }
        Apartment apartment = new Apartment();
        apartment.setId(id);
        return apartment;
    }
}
