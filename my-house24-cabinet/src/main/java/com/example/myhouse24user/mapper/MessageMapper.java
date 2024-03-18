package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.Message;
import com.example.myhouse24user.model.messages.MessageResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {StaffMapper.class})
public interface MessageMapper {

    List<MessageResponse> messageListToMessageResponseList(List<Message> messages);

    MessageResponse messageToMessageResponse(Message message);

}
