package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.messages.MessageResponse;
import com.example.myhouse24user.service.MessagesService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@RequestMapping("/cabinet/messages")
public class MessagesController {

    private final MessagesService messagesService;

    public MessagesController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @GetMapping
    public ModelAndView viewMessages() {
        return new ModelAndView("messages/messages");
    }

    @GetMapping("view-message/{messageId}")
    public ModelAndView viewMessage() {
        return new ModelAndView("messages/view-message");
    }

    @GetMapping("get-messages")
    public ResponseEntity<?> getMessages(@RequestParam int page,
                                         @RequestParam int pageSize,
                                         @RequestParam(required = false, name = "text") String search,
                                         Principal principal) {
        Page<MessageResponse> messageResponsePage =
                messagesService.getApartmentOwnerMessages(principal.getName(), page, pageSize, search);
        return new ResponseEntity<>(messageResponsePage, HttpStatus.OK);
    }

    @GetMapping("get-message/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Long messageId, Principal principal) {
        MessageResponse messageResponse = messagesService.getMessageById(principal.getName(), messageId);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @DeleteMapping("delete-messages")
    public ResponseEntity<?> deleteMessages(@RequestParam Long[] messagesToDelete, Principal principal) {
        try {
            messagesService.deleteMessages(principal, messagesToDelete);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
