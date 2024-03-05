package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.Message;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.messages.MessageResponse;
import com.example.myhouse24admin.model.messages.MessageSendRequest;
import com.example.myhouse24admin.model.messages.MessageTableResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ApartmentOwnerMapper.class,
                StaffMapper.class})
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "sendDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "staff", source = "currentStaff")
    @Mapping(target = "apartmentOwners", source = "apartmentOwnerForSendMessage")
    Message messageSendRequestToMessage(MessageSendRequest messageSendRequest, Staff currentStaff, List<ApartmentOwner> apartmentOwnerForSendMessage);

    List<MessageTableResponse> messageListToMessageResponseTableList(List<Message> messages);

    MessageTableResponse messageToMessageResponseTable(Message message);

    MessageResponse messageToMessageResponse(Message message);
}