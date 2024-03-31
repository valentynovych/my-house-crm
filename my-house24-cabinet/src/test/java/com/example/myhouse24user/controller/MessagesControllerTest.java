package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.messages.OwnerMessageResponse;
import com.example.myhouse24user.service.MessagesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

import static com.example.myhouse24user.config.TestConfig.USER_EMAIL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class MessagesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private MessagesService messagesService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void viewMessages() throws Exception {
        // given
        var request = get("/cabinet/messages")
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
    void viewMessage() throws Exception {
        // given
        var request = get("/cabinet/messages/view-message/%s".formatted(1))
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
    void getMessages() throws Exception {
        Pageable pageable = Pageable.ofSize(10);
        // given
        var request = get("/cabinet/messages/get-messages")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var messageResponse = new OwnerMessageResponse(
                1L,
                "testStaff",
                "testMessage",
                "subjectMessage",
                false,
                Instant.now());
        var ownerMessageResponses = new PageImpl<>(
                List.of(messageResponse), pageable, 1L);

        // when
        doReturn(ownerMessageResponses)
                .when(messagesService).getApartmentOwnerMessages(eq(USER_EMAIL),
                        eq(pageable.getPageNumber()), eq(pageable.getPageSize()), any());
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
                                            "staffFullName": "testStaff",
                                            "text": "testMessage",
                                            "subject": "subjectMessage",
                                            "isRead": false
                                        }
                                    ],
                                    "numberOfElements": 1
                                }
                                """)
                );
        verify(messagesService, times(1)).getApartmentOwnerMessages(eq(USER_EMAIL), eq(pageable.getPageNumber()),
                eq(pageable.getPageSize()), any());
    }

    @Test
    void getMessageById() throws Exception {
        // given
        var request = get("/cabinet/messages/get-message/%s".formatted(1))
                .with(user(userDetails));

        var messageResponse = new OwnerMessageResponse(
                1L,
                "testStaff",
                "testMessage",
                "subjectMessage",
                false,
                Instant.now());

        // when
        doReturn(messageResponse)
                .when(messagesService).getMessageById(eq(USER_EMAIL), eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                            "id": 1,
                                            "staffFullName": "testStaff",
                                            "text": "testMessage",
                                            "subject": "subjectMessage",
                                            "isRead": false
                                }
                                """)
                );
        verify(messagesService, times(1)).getMessageById(eq(USER_EMAIL), eq(1L));
    }

    @Test
    void deleteMessages_WhenRequestIsValid() throws Exception {
        // given
        var request = delete("/cabinet/messages/delete-messages")
                .with(user(userDetails))
                .param("messagesToDelete", "1");

        // when
        doNothing()
                .when(messagesService).deleteMessages(any(Principal.class), eq(new Long[]{1L}));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());
        verify(messagesService, times(1)).deleteMessages(any(Principal.class), eq(new Long[]{1L}));
        clearInvocations(messagesService);
    }

    @Test
    void deleteMessages_WhenRequestIsNotValid() throws Exception {
        // given
        var request = delete("/cabinet/messages/delete-messages")
                .with(user(userDetails))
                .param("messagesToDelete", "1");

        // when
        doThrow(new IllegalArgumentException("Invalid message id"))
                .when(messagesService).deleteMessages(any(Principal.class), eq(new Long[]{1L}));
        this.mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(messagesService, times(1)).deleteMessages(any(Principal.class), eq(new Long[]{1L}));
        clearInvocations(messagesService);
    }

    @Test
    void readMessage_ReadMessageById() throws Exception {
        // given
        var request = post("/cabinet/messages/read-message/%s".formatted(1))
                .with(user(userDetails));

        // when
        doNothing().when(messagesService).readMessage(eq(USER_EMAIL), eq(1L));

        this.mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(messagesService, times(1)).readMessage(eq(USER_EMAIL), eq(1L));
    }

    @Test
    void testReadMessage_ReadAllMessagesByIds() throws Exception {
        // given
        var request = post("/cabinet/messages/read-all-messages")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "wrapper": [1, 2, 3]
                            }
                        """);

        // when
        doNothing().when(messagesService).readAllMessage(eq(USER_EMAIL), eq(List.of(1L, 2L, 3L)));

        this.mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getUnreadMessages() throws Exception {

        Pageable pageable = Pageable.ofSize(10);
        // given
        var request = get("/cabinet/messages/get-unread-messages")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var messageResponse = new OwnerMessageResponse(
                1L,
                "testStaff",
                "testMessage",
                "subjectMessage",
                false,
                Instant.now());
        var ownerMessageResponses = new PageImpl<>(
                List.of(messageResponse), pageable, 1L);

        // when
        doReturn(ownerMessageResponses)
                .when(messagesService).getUnreadMessages(eq(USER_EMAIL),
                        eq(pageable.getPageNumber()), eq(pageable.getPageSize()));
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
                                            "staffFullName": "testStaff",
                                            "text": "testMessage",
                                            "subject": "subjectMessage",
                                            "isRead": false
                                        }
                                    ],
                                    "numberOfElements": 1
                                }
                                """)
                );
        verify(messagesService, times(1)).getUnreadMessages(eq(USER_EMAIL), eq(pageable.getPageNumber()),
                eq(pageable.getPageSize()));
    }
}