package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.messages.MessageResponse;
import com.example.myhouse24admin.model.messages.MessageSendRequest;
import com.example.myhouse24admin.model.messages.MessageTableResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface MessageService {
    void sendNewMessage(MessageSendRequest messageSendRequest);

    Page<MessageTableResponse> getMessages(int page, int pageSize, Map<String, String> searchParams);

    void deleteMessages(Long[] messagesToDelete);

    MessageResponse getMessageById(Long messageId);
}