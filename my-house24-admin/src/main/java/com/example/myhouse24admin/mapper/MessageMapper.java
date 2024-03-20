package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.Message;
import com.example.myhouse24admin.entity.OwnerMessage;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerShortResponse;
import com.example.myhouse24admin.model.messages.MessageResponse;
import com.example.myhouse24admin.model.messages.MessageSendRequest;
import com.example.myhouse24admin.model.messages.MessageTableResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
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
//    @Mapping(target = "apartmentOwners", source = "apartmentOwnerForSendMessage")
    Message messageSendRequestToMessage(MessageSendRequest messageSendRequest, Staff currentStaff);

    List<MessageTableResponse> messageListToMessageResponseTableList(List<Message> messages);

    @Mapping(target = "apartmentOwners", source = "ownerMessages", qualifiedByName = "apartmentOwners")
    MessageTableResponse messageToMessageResponseTable(Message message);

    MessageResponse messageToMessageResponse(Message message);

    @Named(value = "apartmentOwners")
    static List<ApartmentOwnerShortResponse> apartmentOwners(List<OwnerMessage> ownerMessages) {
        List<ApartmentOwnerShortResponse> list = new ArrayList<>();
        for (OwnerMessage ownerMessage : ownerMessages) {
            ApartmentOwner apartmentOwner = ownerMessage.getApartmentOwner();
            ApartmentOwnerShortResponse response =
                    new ApartmentOwnerShortResponse(
                            apartmentOwner.getId(),
                            apartmentOwner.getFirstName() + " " + apartmentOwner.getLastName(),
                            apartmentOwner.getPhoneNumber());
            list.add(response);
        }
        return list;
    }
}
