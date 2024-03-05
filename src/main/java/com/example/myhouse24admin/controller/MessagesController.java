package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.messages.MessageResponse;
import com.example.myhouse24admin.model.messages.MessageSendRequest;
import com.example.myhouse24admin.model.messages.MessageTableResponse;
import com.example.myhouse24admin.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

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

    @GetMapping("view-message/{messageId}")
    public ModelAndView viewNewMessages(@PathVariable Long messageId) {
        return new ModelAndView("messages/view-message");
    }

    @PostMapping("new-message")
    public ResponseEntity<?> sendNewMessage(@ModelAttribute @Valid MessageSendRequest messageSendRequest, HttpServletRequest request) {
        messageService.sendNewMessage(messageSendRequest, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-messages")
    public @ResponseBody ResponseEntity<?> getMessages(@RequestParam int page,
                                                       @RequestParam int pageSize,
                                                       @RequestParam Map<String, String> searchParams) {
        Page<MessageTableResponse> responsePage = messageService.getMessages(page, pageSize, searchParams);
        return new ResponseEntity<>(responsePage, HttpStatus.OK);
    }

    @GetMapping("get-message/{messageId}")
    public @ResponseBody ResponseEntity<?> getMessage(@PathVariable Long messageId) {
        MessageResponse responsePage = messageService.getMessageById(messageId);
        return new ResponseEntity<>(responsePage, HttpStatus.OK);
    }

    @DeleteMapping("delete-messages")
    public ResponseEntity<?> deleteMessages(@RequestParam Long[] messagesToDelete) {
        try {
            messageService.deleteMessages(messagesToDelete);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
