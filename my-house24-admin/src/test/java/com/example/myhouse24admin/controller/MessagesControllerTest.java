package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.messages.MessageResponse;
import com.example.myhouse24admin.model.messages.MessageSendRequest;
import com.example.myhouse24admin.model.messages.MessageTableResponse;
import com.example.myhouse24admin.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessagesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        clearInvocations(messageService);
    }

    @Test
    void viewMessagesTable() throws Exception {
        // given
        var request = get("/my-house/admin/messages")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("messages/messages")
                );
    }

    @Test
    void viewNewMessages() throws Exception {
        // given
        var request = get("/my-house/admin/messages/new-message")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("messages/new-message")
                );
    }

    @Test
    void testViewNewMessages() throws Exception {
        // given
        var request = get("/my-house/admin/messages/view-message/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("messages/view-message")
                );
    }

    @Test
    void sendNewMessage() throws Exception {
        // given
        var messageSendRequest = new MessageSendRequest();
        messageSendRequest.setSubject("subject");
        messageSendRequest.setText("text");
        messageSendRequest.setTextLength(3L);

        var request = post("/admin/messages/new-message")
                .with(user(userDetails))
                .flashAttr("messageSendRequest", messageSendRequest);

        // when
        doNothing().when(messageService).sendNewMessage(eq(messageSendRequest));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk()
                );
        verify(messageService, times(1)).sendNewMessage(eq(messageSendRequest));
    }

    @Test
    void getMessages() throws Exception {
        // given
        var pageable = Pageable.ofSize(10);
        var messageTableResponse = new MessageTableResponse();
        messageTableResponse.setId(1L);
        messageTableResponse.setText("text");
        messageTableResponse.setSubject("subject");

        var request = get("/admin/messages/get-messages")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var tariffResponsePage = new PageImpl<>(
                List.of(messageTableResponse, messageTableResponse), pageable, 2L);

        // when
        doReturn(tariffResponsePage)
                .when(messageService).getMessages(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "content": [
                                    {
                                        "id": 1,
                                        "subject": "subject",
                                        "text": "text"
                                        },
                                        {
                                        "id": 1,
                                        "subject": "subject",
                                        "text": "text"
                                        }
                                        ],
                                    "numberOfElements": 2
                                }
                                """)
                );
        verify(messageService, times(1))
                .getMessages(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());

    }

    @Test
    void getMessage() throws Exception {
        // given
        var messageResponse = new MessageResponse();
        messageResponse.setId(1L);
        messageResponse.setText("text");
        messageResponse.setSubject("subject");

        var request = get("/admin/messages/get-message/1")
                .with(user(userDetails));

        // when
        doReturn(messageResponse)
                .when(messageService).getMessageById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                    {
                                        "id": 1,
                                        "subject": "subject",
                                        "text": "text"
                                    }
                                """)
                );
        verify(messageService, times(1))
                .getMessageById(eq(1L));
    }

    @Test
    void deleteMessages() throws Exception {
        // given
        var request = delete("/admin/messages/delete-messages")
                .with(user(userDetails))
                .param("messagesToDelete", "1,2,3");

        // when
        doNothing()
                .when(messageService).deleteMessages(any(Long[].class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());
        verify(messageService, times(1)).deleteMessages(any(Long[].class));
    }

    @Test
    void deleteMessages_WhenThrowsException() throws Exception {
        // given
        var request = delete("/admin/messages/delete-messages")
                .with(user(userDetails))
                .param("messagesToDelete", "1,2,3");

        // when
        doThrow(new IllegalArgumentException())
                .when(messageService).deleteMessages(any(Long[].class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).deleteMessages(any(Long[].class));
    }
}