package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.entity.ApartmentOwner;
import com.example.myhouse24rest.entity.Message;
import com.example.myhouse24rest.entity.OwnerMessage;
import com.example.myhouse24rest.entity.Staff;
import com.example.myhouse24rest.mapper.OwnerMessageMapper;
import com.example.myhouse24rest.mapper.OwnerMessageMapperImpl;
import com.example.myhouse24rest.model.message.MessageResponse;
import com.example.myhouse24rest.repository.OwnerMessageRepo;
import com.example.myhouse24rest.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private OwnerMessageRepo ownerMessageRepo;
    private MessageService messageService;
    private final List<OwnerMessage> ownerMessages = new ArrayList<>();
    private OwnerMessage ownerMessage;

    @BeforeEach
    void setUp() {
        OwnerMessageMapper ownerMessageMapper = new OwnerMessageMapperImpl();
        messageService = new MessageServiceImpl(ownerMessageRepo, ownerMessageMapper);

        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setId(1L);
        apartmentOwner.setEmail("test@example.com");
        apartmentOwner.setFirstName("Test");
        apartmentOwner.setLastName("User");

        Staff staff = new Staff();
        staff.setId(1L);
        staff.setEmail("test@example.com");
        staff.setFirstName("Test");
        staff.setLastName("User");

        for (int i = 0; i < 5; i++) {
            Message message = new Message();
            message.setId((long) (i + 1));
            message.setText("Test Message");
            message.setSubject("Test Subject");
            message.setSendDate(Instant.now());
            message.setStaff(staff);

            OwnerMessage ownerMessage = new OwnerMessage();
            ownerMessage.setId((long) (i + 1));
            ownerMessage.setRead(false);
            ownerMessage.setDeleted(false);
            ownerMessage.setApartmentOwner(apartmentOwner);
            ownerMessage.setMessage(message);

            ownerMessages.add(ownerMessage);
        }

        ownerMessage = ownerMessages.get(0);
    }

    @Test
    public void testGetMessageById_ifMessageExists() {
        Long messageId = 1L;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        when(ownerMessageRepo
                .findOwnerMessageByMessageIdAndApartmentOwner_Email(messageId, "test@example.com"))
                .thenReturn(Optional.of(ownerMessage));

        MessageResponse result = messageService.getMessageById(messageId, principal);

        assertEquals(messageId, result.messageId());
        assertEquals("Test Message", result.text());
        verify(ownerMessageRepo, times(1))
                .findOwnerMessageByMessageIdAndApartmentOwner_Email(messageId, "test@example.com");
    }

    @Test
    public void testGetMessageById_ifMessageNotExists() {
        Long messageId = 1L;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        when(ownerMessageRepo
                .findOwnerMessageByMessageIdAndApartmentOwner_Email(messageId, "test@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> messageService.getMessageById(messageId, principal));

        verify(ownerMessageRepo, times(1))
                .findOwnerMessageByMessageIdAndApartmentOwner_Email(messageId, "test@example.com");
    }

    @Test
    public void testReadMessageById() {
        Long messageId = 1L;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        when(ownerMessageRepo
                .findOwnerMessageByMessageIdAndApartmentOwner_Email(messageId, "test@example.com")).thenReturn(Optional.of(ownerMessage));

        messageService.readMessageById(messageId, principal);

        assertTrue(ownerMessage.isRead());
        verify(ownerMessageRepo, times(1)).save(ownerMessage);
    }

    @Test
    public void testGetUnreadMessages() {
        int page = 0;
        int pageSize = 10;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        Page<OwnerMessage> ownerMessagesPage = new PageImpl<>(ownerMessages);
        when(ownerMessageRepo
                .findOwnerMessagesByApartmentOwner_EmailAndReadFalse("test@example.com", page, pageSize, Pageable.ofSize(pageSize).withPage(page))).thenReturn(ownerMessagesPage);

        Page<MessageResponse> result = messageService.getUnreadMessages(principal, page, pageSize);

        assertEquals(5, result.getContent().size());
        verify(ownerMessageRepo, times(1)).findOwnerMessagesByApartmentOwner_EmailAndReadFalse(
                "test@example.com", page, pageSize, Pageable.ofSize(pageSize).withPage(page));
    }

    @Test
    public void testGetAllMessages() {
        int page = 0;
        int pageSize = 10;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        Page<OwnerMessage> ownerMessagesPage = new PageImpl<>(ownerMessages);
        when(ownerMessageRepo.findOwnerMessagesByApartmentOwner_Email("test@example.com", Pageable.ofSize(pageSize).withPage(page))).thenReturn(ownerMessagesPage);

        Page<MessageResponse> result = messageService.getAllMessages(principal, page, pageSize);

        assertEquals(5, result.getContent().size());
        verify(ownerMessageRepo, times(1)).findOwnerMessagesByApartmentOwner_Email(
                "test@example.com", Pageable.ofSize(pageSize).withPage(page));
    }
}