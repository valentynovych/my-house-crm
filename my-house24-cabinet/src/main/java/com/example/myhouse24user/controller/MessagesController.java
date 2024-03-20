package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.messages.ListLongWrapper;
import com.example.myhouse24user.model.messages.OwnerMessageResponse;
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
        Page<OwnerMessageResponse> messageResponsePage =
                messagesService.getApartmentOwnerMessages(principal.getName(), page, pageSize, search);
        return new ResponseEntity<>(messageResponsePage, HttpStatus.OK);
    }

    @GetMapping("get-message/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Long messageId, Principal principal) {
        OwnerMessageResponse messageResponse = messagesService.getMessageById(principal.getName(), messageId);
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

    @PostMapping("read-message/{messageId}")
    public ResponseEntity<?> readMessage(@PathVariable Long messageId, Principal principal) {
        messagesService.readMessage(principal.getName(), messageId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("read-all-messages")
    public ResponseEntity<?> readMessage(@RequestBody ListLongWrapper wrapper, Principal principal) {
        messagesService.readAllMessage(principal.getName(), wrapper.getIdsToMarkAsRead());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-unread-messages")
    public ResponseEntity<Page<OwnerMessageResponse>> getUnreadMessages(Principal principal,
                                               @RequestParam int page,
                                               @RequestParam int pageSize) {
        Page<OwnerMessageResponse> unreadMessages =
                messagesService.getUnreadMessages(principal.getName(), page, pageSize);
        return new ResponseEntity<>(unreadMessages, HttpStatus.OK);
    }
}
