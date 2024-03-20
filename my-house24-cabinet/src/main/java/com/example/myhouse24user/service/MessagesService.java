package com.example.myhouse24user.service;

import com.example.myhouse24user.model.messages.OwnerMessageResponse;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

public interface MessagesService {

    Page<OwnerMessageResponse> getApartmentOwnerMessages(String name, int page, int pageSize, String search);

    OwnerMessageResponse getMessageById(String name, Long messageId);

    void deleteMessages(Principal principal, Long[] messagesToDelete);

    void readMessage(String ownerEmail, Long messageId);

    Page<OwnerMessageResponse> getUnreadMessages(String name, int page, int pageSize);

    void readAllMessage(String ownerEmail, List<Long> idsToMarkAsRead);
}
