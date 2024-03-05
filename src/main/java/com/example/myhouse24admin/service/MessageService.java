package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.messages.MessageSendRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface MessageService {
    void sendNewMessage(MessageSendRequest messageSendRequest, HttpServletRequest request);
}
