package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.Message;
import com.example.myhouse24admin.entity.OwnerMessage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OwnerMessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "apartmentOwner", source = "owner")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "read", constant = "false")
    OwnerMessage createOwnerMessageFromMessageAndApartmentOwner(Message message, ApartmentOwner owner);
}
