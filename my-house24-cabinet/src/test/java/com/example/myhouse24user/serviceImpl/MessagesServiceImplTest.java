package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.Message;
import com.example.myhouse24user.entity.OwnerMessage;
import com.example.myhouse24user.mapper.OwnerMessageMapper;
import com.example.myhouse24user.model.messages.OwnerMessageResponse;
import com.example.myhouse24user.repository.OwnerMessageRepo;
import com.example.myhouse24user.specification.OwnerMessageSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.myhouse24user.config.TestConfig.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagesServiceImplTest {

    @Mock
    private OwnerMessageRepo ownerMessageRepo;
    @Mock
    private OwnerMessageMapper ownerMessageMapper;
    @Mock
    private Principal principal;
    @InjectMocks
    private MessagesServiceImpl messagesService;
    private List<OwnerMessage> ownerMessages;
    private List<OwnerMessageResponse> ownerMessageResponses;

    @BeforeEach
    void setUp() {
        ownerMessages = new ArrayList<>();
        ownerMessageResponses = new ArrayList<>();

        var message = new Message();
        message.setId(1L);
        message.setSendDate(Instant.now());
        message.setText("textMessage");
        message.setSubject("subjectMessage");
        message.setDeleted(false);

        var apartmentOwner = new ApartmentOwner();
        apartmentOwner.setId(1L);

        for (int i = 0; i < 5; i++) {
            var ownerMessage = new OwnerMessage();
            ownerMessage.setId((long) i);
            ownerMessage.setMessage(message);
            ownerMessage.setApartmentOwner(apartmentOwner);
            ownerMessage.setRead(false);
            ownerMessages.add(ownerMessage);
            ownerMessageResponses.add(new OwnerMessageResponse(
                    ownerMessage.getId(),
                    "staffName",
                    message.getText(),
                    message.getSubject(),
                    ownerMessage.isRead(),
                    message.getSendDate()
            ));
        }
    }

    @Test
    void getApartmentOwnerMessages() {
        // when
        when(ownerMessageRepo.findAll(any(OwnerMessageSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(ownerMessages));
        when(ownerMessageMapper.ownerMessageListToMessageResponseList(ownerMessages))
                .thenReturn(ownerMessageResponses);

        Page<OwnerMessageResponse> apartmentOwnerMessages = messagesService.getApartmentOwnerMessages(USER_EMAIL, 0, 10, "search");

        // then
        List<OwnerMessageResponse> content = apartmentOwnerMessages.getContent();
        assertFalse(content.isEmpty());
        assertEquals(5, apartmentOwnerMessages.getTotalElements());
        assertEquals(5, content.size());
    }

    @Test
    void getMessageById() {

        // when
        when(ownerMessageRepo.findOne(any(OwnerMessageSpecification.class)))
                .thenReturn(Optional.of(ownerMessages.get(0)));
        when(ownerMessageMapper.ownerMessageToMessageResponse(ownerMessages.get(0)))
                .thenReturn(ownerMessageResponses.get(0));

        OwnerMessageResponse messageResponse = messagesService.getMessageById(USER_EMAIL, 1L);

        // then
        assertNotNull(messageResponse);
        assertEquals(ownerMessageResponses.get(0), messageResponse);

    }

    @Test
    void deleteMessages_WhenRequestListSizeIsEqualRepositoryResultSize() {
        // given
        ArgumentCaptor<List<OwnerMessage>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        // when
        when(principal.getName())
                .thenReturn(USER_EMAIL);
        when(ownerMessageRepo.findAllByIdIsInAndApartmentOwner_Email(eq(List.of(1L, 2L, 3L, 4L, 5L)), eq(USER_EMAIL)))
                .thenReturn(ownerMessages);
        messagesService.deleteMessages(principal, new Long[]{1L, 2L, 3L, 4L, 5L});

        // then
        verify(ownerMessageRepo, times(1)).deleteAll(argumentCaptor.capture());
        List<OwnerMessage> argumentCaptorValue = argumentCaptor.getValue();
        for (OwnerMessage ownerMessage : argumentCaptorValue) {
            assertTrue(ownerMessage.isDeleted());
        }
    }

    @Test
    void deleteMessages_WhenRequestListSizeIsNotEqualRepositoryResultSize() {
        // given
        ownerMessages.remove(4);
        // when
        when(principal.getName())
                .thenReturn(USER_EMAIL);
        when(ownerMessageRepo.findAllByIdIsInAndApartmentOwner_Email(
                eq(List.of(1L, 2L, 3L, 4L, 5L)), eq(USER_EMAIL)))
                .thenReturn(ownerMessages);

        // then
        assertThrows(IllegalArgumentException.class,
                () -> messagesService.deleteMessages(principal, new Long[]{1L, 2L, 3L, 4L, 5L}));

    }

    @Test
    void readMessage_WhenFound() {
        // given
        ArgumentCaptor<OwnerMessage> argumentCaptor = ArgumentCaptor.forClass(OwnerMessage.class);
        // when
        when(ownerMessageRepo.findOne(any(OwnerMessageSpecification.class)))
                .thenReturn(Optional.of(ownerMessages.get(0)));
        messagesService.readMessage(USER_EMAIL, 1L);

        // then
        verify(ownerMessageRepo, times(1)).save(argumentCaptor.capture());
        OwnerMessage argumentCaptorValue = argumentCaptor.getValue();
        assertTrue(argumentCaptorValue.isRead());
    }

    @Test
    void readMessage_WhenNotFound() {
        // when
        when(ownerMessageRepo.findOne(any(OwnerMessageSpecification.class)))
                .thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class,
                () -> messagesService.readMessage(USER_EMAIL, 1L));
    }

    @Test
    void getUnreadMessages() {

        // when
        when(ownerMessageRepo.findAll(any(OwnerMessageSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(ownerMessages));
        when(ownerMessageMapper.ownerMessageListToMessageResponseList(ownerMessages))
                .thenReturn(ownerMessageResponses);

        Page<OwnerMessageResponse> apartmentOwnerMessages =
                messagesService.getUnreadMessages(USER_EMAIL, 0, 10);

        // then
        List<OwnerMessageResponse> content = apartmentOwnerMessages.getContent();
        assertFalse(content.isEmpty());
        assertEquals(5, apartmentOwnerMessages.getTotalElements());
        assertEquals(5, content.size());
    }

    @Test
    void readAllMessage_WhenRequestListSizeIsEqualRepositoryResultSize() {
        // given
        ArgumentCaptor<List<OwnerMessage>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        // when
        when(ownerMessageRepo.findAllByIdIsInAndApartmentOwner_Email(eq(List.of(1L, 2L, 3L, 4L, 5L)), eq(USER_EMAIL)))
                .thenReturn(ownerMessages);
        messagesService.readAllMessage(USER_EMAIL, List.of(1L, 2L, 3L, 4L, 5L));

        // then
        verify(ownerMessageRepo, times(1)).saveAll(argumentCaptor.capture());
        List<OwnerMessage> argumentCaptorValue = argumentCaptor.getValue();
        for (OwnerMessage ownerMessage : argumentCaptorValue) {
            assertTrue(ownerMessage.isRead());
        }
    }

    @Test
    void readAllMessage_WhenRequestListSizeIsNotEqualRepositoryResultSize() {
        // given
        ownerMessages.remove(4);

        // when
        when(ownerMessageRepo.findAllByIdIsInAndApartmentOwner_Email(eq(List.of(1L, 2L, 3L, 4L, 5L)), eq(USER_EMAIL)))
                .thenReturn(ownerMessages);
        // then
        assertThrows(IllegalArgumentException.class,
                () -> messagesService.readAllMessage(USER_EMAIL, List.of(1L, 2L, 3L, 4L, 5L)));

    }
}