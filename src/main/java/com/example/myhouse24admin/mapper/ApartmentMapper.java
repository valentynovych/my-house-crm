package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.model.apartments.ApartmentExtendResponse;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ApartmentOwnerMapper.class,
                HouseMapper.class,
                SectionMapper.class,
                FloorMapper.class,
                TariffMapper.class,
                PersonalAccountMapper.class})
public interface ApartmentMapper {
    @Mapping(target = "owner.id", source = "ownerId")
    @Mapping(target = "house.id", source = "houseId")
    @Mapping(target = "section.id", source = "sectionId")
    @Mapping(target = "floor.id", source = "floorId")
    @Mapping(target = "tariff.id", source = "tariffId")
    @Mapping(target = "personalAccount.id", source = "personalAccountId")
    @Mapping(target = "balance", expression = "java(java.math.BigDecimal.ZERO)")
    Apartment apartmentAddRequestToApartment(ApartmentAddRequest apartmentAddRequest);

    List<ApartmentResponse> apartmentListToApartmentResponseList(List<Apartment> apartments);

    ApartmentResponse apartmentToApartmentResponse(Apartment apartments);

    @Mapping(target = "tariff", source = "tariff")
    ApartmentExtendResponse apartmentToApartmentExtendResponse(Apartment apartment);

    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "setNewOwner")
    @Mapping(target = "house", source = "houseId", qualifiedByName = "setNewHouse")
    @Mapping(target = "section", source = "sectionId", qualifiedByName = "setNewSection")
    @Mapping(target = "floor", source = "floorId", qualifiedByName = "setNewFloor")
    @Mapping(target = "tariff", source = "tariffId", qualifiedByName = "setNewTariff")
    @Mapping(target = "personalAccount", ignore = true)
    void updateApartmentFromApartmentRequest(@MappingTarget Apartment apartment, ApartmentAddRequest apartmentRequest);

    @Named(value = "setNewOwner")
    static ApartmentOwner setNewOwner(Long ownerId) {
        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setId(ownerId);
        return apartmentOwner;
    }

    @Named(value = "setNewHouse")
    static House setNewHouse(Long houseId) {
        House house = new House();
        house.setId(houseId);
        return house;
    }

    @Named(value = "setNewSection")
    static Section setNewSection(Long sectionId) {
        Section section = new Section();
        section.setId(sectionId);
        return section;
    }

    @Named(value = "setNewFloor")
    static Floor setNewFloor(Long floorId) {
        Floor floor = new Floor();
        floor.setId(floorId);
        return floor;
    }

    @Named(value = "setNewTariff")
    static Tariff setNewTariff(Long tariffId) {
        Tariff tariff = new Tariff();
        tariff.setId(tariffId);
        return tariff;
    }
}
