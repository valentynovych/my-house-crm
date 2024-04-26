package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.model.error.CustomErrorResponse;
import com.example.myhouse24rest.model.message.MessageResponse;
import com.example.myhouse24rest.model.message.MessageResponsePage;
import com.example.myhouse24rest.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/v1/messages")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Messages", description = "Message API")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(
            summary = "Get message by id",
            description = "Get apartment owner message by id, id - is a path variable, must be positive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Not Found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))})
    })
    @GetMapping("get-message/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Long messageId, Principal principal) {
        MessageResponse messageResponse = messageService.getMessageById(messageId, principal);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Read message by id",
            description = "Send request for marking message as read, by id, id - is a path variable, must be positive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Not Found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))})
    })
    @PatchMapping("read-message/{messageId}")
    public ResponseEntity<?> readMessageById(@PathVariable Long messageId, Principal principal) {
        messageService.readMessageById(messageId, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Get all unread messages",
            description = "Get all unread messages, with pagination by page and pageSize")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponsePage.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))})
    })
    @GetMapping("get-unread-messages")
    public ResponseEntity<?> getUnreadMessages(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               Principal principal) {
        Page<MessageResponse> unreadMessages = messageService.getUnreadMessages(principal, page, pageSize);
        return new ResponseEntity<>(unreadMessages, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all messages",
            description = "Get all apartment owner messages, with pagination by page and pageSize")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponsePage.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))})
    })
    @GetMapping("get-all-messages")
    public ResponseEntity<?> getAllMessages(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            Principal principal) {
        Page<MessageResponse> allMessages = messageService.getAllMessages(principal, page, pageSize);
        return new ResponseEntity<>(allMessages, HttpStatus.OK);
    }
}
