package com.example.myhouse24rest.mapper;

import com.example.myhouse24rest.entity.OwnerMessage;
import com.example.myhouse24rest.model.message.MessageResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OwnerMessageMapper {

    @Mapping(target = "messageId", source = "ownerMessage.message.id")
    @Mapping(target = "sendDate", source = "ownerMessage.message.sendDate", qualifiedByName = "toLocalDateTime")
    @Mapping(target = "subject", source = "ownerMessage.message.subject")
    @Mapping(target = "text", source = "ownerMessage.message.text")
    @Mapping(target = "fromStaff", expression = "java(ownerMessage.getMessage().getStaff().getFullName())")
    MessageResponse ownerMessageToMessageResponse(OwnerMessage ownerMessage);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    List<MessageResponse> ownerMessagesToMessageResponses(List<OwnerMessage> ownerMessages);
}
