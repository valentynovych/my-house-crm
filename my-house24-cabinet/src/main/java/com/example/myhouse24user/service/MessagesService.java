package com.example.myhouse24user.service;

import com.example.myhouse24user.model.messages.MessageResponse;
import org.springframework.data.domain.Page;

import java.security.Principal;

public interface MessagesService {

    Page<MessageResponse> getApartmentOwnerMessages(String name, int page, int pageSize, String search);

    MessageResponse getMessageById(String name, Long messageId);

    void deleteMessages(Principal principal, Long[] messagesToDelete);
}
