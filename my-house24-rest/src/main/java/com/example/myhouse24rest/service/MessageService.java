package com.example.myhouse24rest.service;

import com.example.myhouse24rest.model.message.MessageResponse;
import org.springframework.data.domain.Page;

import java.security.Principal;

public interface MessageService {
    MessageResponse getMessageById(Long messageId, Principal principal);

    void readMessageById(Long messageId, Principal principal);

    Page<MessageResponse> getUnreadMessages(Principal principal, int page, int pageSize);

    Page<MessageResponse> getAllMessages(Principal principal, int page, int pageSize);
}
