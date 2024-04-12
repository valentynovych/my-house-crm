package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.model.message.MessageResponse;
import com.example.myhouse24rest.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MessageService messageService;
    private List<MessageResponse> messages;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = Pageable.ofSize(10);
        messages = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            messages.add(new MessageResponse(
                    (long) (i + 1),
                    LocalDateTime.now(),
                    "messageSubject",
                    "messageText",
                    "senderName",
                    true
            ));
        }
    }

    @Test
    void getMessageById_WhenAuthorized_ShouldReturnMessage() throws Exception {

        // given
        var messageResponse = messages.get(0);
        var request = get("/v1/messages/get-message/%s".formatted(messageResponse.messageId()))
                .with(jwt().jwt(builder -> builder.claim("role", "OWNER")));

        // when
        when(messageService.getMessageById(eq(messageResponse.messageId()), any(Principal.class)))
                .thenReturn(messageResponse);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "messageId": 1,
                                    "subject": "messageSubject",
                                    "text": "messageText",
                                    "fromStaff": "senderName",
                                    "read": true
                                }
                                """)
                );
        verify(messageService, times(1)).getMessageById(eq(messageResponse.messageId()), any(Principal.class));

    }

    @Test
    void readMessageById() throws Exception {
        // given
        var messageResponse = messages.get(0);
        var request = patch("/v1/messages/read-message/%s".formatted(messageResponse.messageId()))
                .with(jwt().jwt(builder -> builder.claim("role", "OWNER")));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());
        verify(messageService, times(1)).readMessageById(eq(messageResponse.messageId()), any(Principal.class));

    }

    @Test
    void getUnreadMessages_WhenAuthorized_ShouldReturnAllUnreadMessages() throws Exception {
        // given
        var request = get("/v1/messages/get-unread-messages")
                .with(jwt().jwt(builder -> builder.claim("role", "OWNER")))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var messageResponses = new PageImpl<>(messages, pageable, messages.size());

        // when
        when(messageService.getUnreadMessages(any(Principal.class), eq(pageable.getPageNumber()), eq(pageable.getPageSize())))
                .thenReturn(messageResponses);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "content":[{},{},{},{},{}],
                                    "totalElements":5
                                }
                                """)
                );
        verify(messageService, times(1)).getUnreadMessages(any(Principal.class), eq(pageable.getPageNumber()),
                eq(pageable.getPageSize()));
    }

    @Test
    void getAllMessages_WhenAuthorized_ShouldReturnAllMessages() throws Exception {
        var request = get("/v1/messages/get-all-messages")
                .with(jwt().jwt(builder -> builder.claim("role", "OWNER")))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var messageResponses = new PageImpl<>(messages, pageable, messages.size());

        // when
        when(messageService.getAllMessages(any(Principal.class), eq(pageable.getPageNumber()), eq(pageable.getPageSize())))
                .thenReturn(messageResponses);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"content":[{},{},{},{},{}],"totalElements":5}
                                """)
                );
        verify(messageService, times(1)).getAllMessages(any(Principal.class), eq(pageable.getPageNumber()),
                eq(pageable.getPageSize()));
    }
}