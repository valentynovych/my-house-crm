package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.MasterRequest;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestTableResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ApartmentMapper.class,
                StaffMapper.class})
public interface MasterRequestMapper {


    @Mapping(target = "creationDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "apartment", source = "apartmentId", qualifiedByName = "setApartment")
    @Mapping(target = "staff", source = "masterId", qualifiedByName = "setStaff")
    MasterRequest masterRequestAddRequestToMasterRequest(MasterRequestAddRequest request);

    @Named(value = "setApartment")
    static Apartment setApartment(Long apartmentId) {
        Apartment apartment = null;
        if (apartmentId != null) {
            apartment = new Apartment();
            apartment.setId(apartmentId);
        }
        return apartment;
    }

    @Named(value = "setStaff")
    static Staff setStaff(Long staffId) {
        Staff staff = null;
        if (staffId != null) {
            staff = new Staff();
            staff.setId(staffId);
        }
        return staff;
    }

    List<MasterRequestTableResponse> masterRequestListToMasterRequestTableResponseList(List<MasterRequest> masterRequests);

    @Mapping(target = "master", source = "staff")
    @Mapping(target = "apartmentOwnerPhone", source = "apartment.owner.phoneNumber")
    MasterRequestTableResponse masterRequestToMasterRequestTableResponse(MasterRequest masterRequest);
}
