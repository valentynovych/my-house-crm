package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.messages.MessageSendRequest;
import com.example.myhouse24admin.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/messages")
public class MessagesController {

    private final MessageService messageService;

    public MessagesController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ModelAndView viewMessagesTable() {
        return new ModelAndView("messages/messages");
    }

    @GetMapping("new-message")
    public ModelAndView viewNewMessages() {
        return new ModelAndView("messages/new-message");
    }

    @PostMapping("new-message")
    public ResponseEntity<?> sendNewMessage(@ModelAttribute @Valid MessageSendRequest messageSendRequest, HttpServletRequest request) {
        messageService.sendNewMessage(messageSendRequest, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
