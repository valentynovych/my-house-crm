package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.model.message.MessageResponse;
import com.example.myhouse24rest.service.MessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/messages")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("get-message/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Long messageId, Principal principal) {
        MessageResponse messageResponse = messageService.getMessageById(messageId, principal);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @PatchMapping("read-message/{messageId}")
    public ResponseEntity<?> readMessageById(@PathVariable Long messageId, Principal principal) {
        messageService.readMessageById(messageId, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-unread-messages")
    public ResponseEntity<?> getUnreadMessages(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               Principal principal) {
        Page<MessageResponse> unreadMessages = messageService.getUnreadMessages(principal, page, pageSize);
        return new ResponseEntity<>(unreadMessages, HttpStatus.OK);
    }
}
