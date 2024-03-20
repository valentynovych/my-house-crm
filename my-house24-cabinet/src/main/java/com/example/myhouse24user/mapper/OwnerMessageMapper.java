package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.OwnerMessage;
import com.example.myhouse24user.model.messages.OwnerMessageResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OwnerMessageMapper {
    List<OwnerMessageResponse> ownerMessageListToMessageResponseList(List<OwnerMessage> ownerMessages);

    @Mapping(target = "staffFullName", expression = "java(ownerMessage.getMessage().getStaff().getFullName())")
    @Mapping(target = "text", source = "message.text")
    @Mapping(target = "subject", source = "message.subject")
    @Mapping(target = "sendDate", source = "message.sendDate")
    @Mapping(target = "isRead", expression = "java(ownerMessage.isRead())")
    OwnerMessageResponse ownerMessageToMessageResponse(OwnerMessage ownerMessage);
}
