package com.example.myhouse24admin.model.meterReadings;

public record ReadingsApartmentResponse(
        HouseNameResponse houseNameResponse,
        SectionNameResponse sectionNameResponse,
        ApartmentNumberResponse apartmentNumberResponse
) {
}
