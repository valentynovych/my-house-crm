package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.mapper.MessageMapper;
import com.example.myhouse24admin.mapper.OwnerMessageMapper;
import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerShortResponse;
import com.example.myhouse24admin.model.messages.MessageResponse;
import com.example.myhouse24admin.model.messages.MessageSendRequest;
import com.example.myhouse24admin.model.messages.MessageTableResponse;
import com.example.myhouse24admin.model.staff.StaffShortResponse;
import com.example.myhouse24admin.repository.MessageRepo;
import com.example.myhouse24admin.repository.OwnerMessageRepo;
import com.example.myhouse24admin.service.ApartmentService;
import com.example.myhouse24admin.service.MailService;
import com.example.myhouse24admin.service.StaffService;
import com.example.myhouse24admin.specification.ApartmentSpecification;
import com.example.myhouse24admin.specification.MessageSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepo messageRepo;
    @Mock
    private StaffService staffService;
    @Mock
    private ApartmentService apartmentService;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private MailService mailService;
    @Mock
    private OwnerMessageMapper ownerMessageMapper;
    @Mock
    private OwnerMessageRepo ownerMessageRepo;
    @InjectMocks
    private MessageServiceImpl messageService;

    private static List<Apartment> apartmentList;
    private static List<OwnerMessage> ownerMessageList;
    private static Message message;
    private static MessageSendRequest messageSendRequest;
    private static Staff currentStaff;

    @BeforeEach
    void setUp() {
        apartmentList = new ArrayList<>();
        ownerMessageList = new ArrayList<>();

        messageSendRequest = new MessageSendRequest();
        messageSendRequest.setSubject("testSubject");
        messageSendRequest.setText("testMessage");

        currentStaff = new Staff();
        currentStaff.setId(1L);
        currentStaff.setEmail("testStaffEmail");
        currentStaff.setFirstName("testStaffFirstName");
        currentStaff.setLastName("testStaffLastName");

        message = new Message();
        message.setId(1L);
        message.setSubject(messageSendRequest.getSubject());
        message.setText(messageSendRequest.getText());
        message.setStaff(currentStaff);
        message.setDeleted(false);
        message.setSendDate(Instant.now());

        for (int i = 0; i < 12; i++) {
            Apartment apartment = new Apartment();
            apartment.setApartmentNumber("0000" + i);
            apartment.setId(i + 1L);

            ApartmentOwner apartmentOwner = new ApartmentOwner();
            apartmentOwner.setId(i + 1L);
            apartmentOwner.setApartments(List.of(apartment));
            apartmentOwner.setEmail("testEmail" + i);
            apartmentOwner.setFirstName("testFirstName" + i);
            apartmentOwner.setLastName("testLastName" + i);
            apartment.setOwner(apartmentOwner);
            apartmentList.add(apartment);

            OwnerMessage ownerMessage = new OwnerMessage();
            ownerMessage.setId(i + 1L);
            ownerMessage.setApartmentOwner(apartmentOwner);
            ownerMessage.setMessage(message);
            ownerMessage.setRead(false);
            ownerMessage.setDeleted(false);
            ownerMessageList.add(ownerMessage);
        }
    }

    @Test
    void sendNewMessage_WhenForArrears() {
        // given
        messageSendRequest.setForArrears(true);

        // when
        when(apartmentService.getAllApartmentsBy(any(Pageable.class), anyList(), any(ApartmentSpecification.class)))
                .thenReturn(apartmentList);
        when(staffService.getCurrentStaff())
                .thenReturn(currentStaff);
        when(messageMapper.messageSendRequestToMessage(messageSendRequest, currentStaff))
                .thenReturn(message);
        doNothing().when(mailService).sendMessage(anyString(), anyString(), anyString(), eq(currentStaff));
        when(ownerMessageMapper.createOwnerMessageFromMessageAndApartmentOwner(eq(message), any(ApartmentOwner.class)))
                .thenAnswer(new Answer<OwnerMessage>() {
                    private int invocation = 0;

                    @Override
                    public OwnerMessage answer(InvocationOnMock invocationOnMock) throws Throwable {
                        OwnerMessage ownerMessage = ownerMessageList.get(invocation);
                        invocation++;
                        return ownerMessage;
                    }
                });

        // call the method
        int countSentMessages = messageService.sendNewMessage(messageSendRequest);

        // then
        assertEquals(12, countSentMessages);

        verify(apartmentService, times(1)).getAllApartmentsBy(any(Pageable.class), any(), any(ApartmentSpecification.class));
        verify(staffService, times(1)).getCurrentStaff();
        verify(messageMapper, times(1)).messageSendRequestToMessage(messageSendRequest, currentStaff);
        verify(mailService, times(12)).sendMessage(anyString(), anyString(), anyString(), eq(currentStaff));
        verify(ownerMessageMapper, times(12)).createOwnerMessageFromMessageAndApartmentOwner(eq(message), any(ApartmentOwner.class));
        verify(ownerMessageRepo).saveAll(anyList());
    }

    @Test
    void sendNewMessage_WhenMessageForSelectedApartment() {
        // given
        messageSendRequest.setForArrears(false);
        messageSendRequest.setApartment(1L);
        messageSendRequest.setHouse(1L);
        messageSendRequest.setSection(1L);
        messageSendRequest.setFloor(1L);

        // when
        when(apartmentService.getAllApartmentsBy(any(Pageable.class), anyList(), any(ApartmentSpecification.class)))
                .thenReturn(apartmentList);
        when(staffService.getCurrentStaff())
                .thenReturn(currentStaff);
        when(messageMapper.messageSendRequestToMessage(messageSendRequest, currentStaff))
                .thenReturn(message);
        doNothing().when(mailService).sendMessage(anyString(), anyString(), anyString(), eq(currentStaff));
        when(ownerMessageMapper.createOwnerMessageFromMessageAndApartmentOwner(eq(message), any(ApartmentOwner.class)))
                .thenAnswer(new Answer<OwnerMessage>() {
                    private int invocation = 0;

                    @Override
                    public OwnerMessage answer(InvocationOnMock invocationOnMock) throws Throwable {
                        OwnerMessage ownerMessage = ownerMessageList.get(invocation);
                        invocation++;
                        return ownerMessage;
                    }
                });

        // call the method
        messageService.sendNewMessage(messageSendRequest);

        // then
        verify(apartmentService, times(1)).getAllApartmentsBy(any(Pageable.class), any(), any(ApartmentSpecification.class));
        verify(staffService, times(1)).getCurrentStaff();
        verify(messageMapper, times(1)).messageSendRequestToMessage(messageSendRequest, currentStaff);
        verify(mailService, times(12)).sendMessage(anyString(), anyString(), anyString(), eq(currentStaff));
        verify(ownerMessageMapper, times(12)).createOwnerMessageFromMessageAndApartmentOwner(eq(message), any(ApartmentOwner.class));
        verify(ownerMessageRepo).saveAll(anyList());
    }

    @Test
    void getMessages() {
        // given
        Map<String, String> searchParams = new HashMap<>() {{
            put("text", "testText");
        }};

        List<Message> messageList = new ArrayList<>();
        List<MessageTableResponse> messageTableResponseList = new ArrayList<>();
        for (int i = 0; i < ownerMessageList.size(); i++) {
            OwnerMessage ownerMessage = ownerMessageList.get(i);

            Message message = new Message();
            message.setId(ownerMessage.getId());
            Message originMessage = ownerMessage.getMessage();
            message.setText(originMessage.getText());
            message.setSubject(originMessage.getSubject());
            message.setStaff(currentStaff);
            message.setSendDate(originMessage.getSendDate());
            messageList.add(message);

            MessageTableResponse messageTableResponse = new MessageTableResponse();
            messageTableResponse.setId(ownerMessage.getId());
            messageTableResponse.setText(originMessage.getText());
            messageTableResponse.setSubject(originMessage.getSubject());
            messageTableResponse.setSendDate(originMessage.getSendDate());

            ApartmentOwner apartmentOwner = apartmentList.get(i).getOwner();
            messageTableResponse.setApartmentOwners(List.of(new ApartmentOwnerShortResponse(
                    apartmentOwner.getId(),
                    apartmentOwner.getFirstName(),
                    apartmentOwner.getLastName()
            )));
            messageTableResponseList.add(messageTableResponse);
        }
        Page<Message> page = new PageImpl<>(messageList.subList(0, 10), PageRequest.of(0, 10), messageList.size());

        // when
        when(messageRepo.findAll(any(MessageSpecification.class), any(Pageable.class)))
                .thenReturn(page);
        when(messageMapper.messageListToMessageResponseTableList(eq(page.getContent())))
                .thenReturn(messageTableResponseList.subList(0, 10));

        // call the method
        Page<MessageTableResponse> messages = messageService.getMessages(0, 10, searchParams);

        // then
        assertEquals(12, messages.getTotalElements());
        assertEquals(10, messages.getContent().size());
        verify(messageRepo, times(1)).findAll(any(MessageSpecification.class), any(Pageable.class));
        verify(messageMapper, times(1)).messageListToMessageResponseTableList(eq(page.getContent()));
    }

    @Test
    void deleteMessages() {
        // given
        Long[] messagesToDelete = new Long[]{1L, 2L, 3L};
        List<Message> messageList = ownerMessageList.stream()
                .map(OwnerMessage::getMessage)
                .limit(3)
                .toList();

        // when
        when(messageRepo.findAllById(anyList()))
                .thenReturn(messageList);
        when(ownerMessageRepo.findAllByMessageIn(anyList()))
                .thenReturn(ownerMessageList.subList(0, 3));

        // call the method
        messageService.deleteMessages(messagesToDelete);

        // then
        verify(messageRepo, times(1)).findAllById(anyList());
        verify(ownerMessageRepo, times(1)).findAllByMessageIn(anyList());
        verify(ownerMessageRepo, times(1)).deleteAll(anyList());
        verify(messageRepo, times(1)).deleteAll(anyList());
    }

    @Test
    void deleteMessages_WhenFindMessageSizeNotEqualsRequestArraySize() {
        // given
        Long[] messagesToDelete = new Long[]{1L, 2L, 3L};
        List<Message> messageList = ownerMessageList.stream()
                .map(OwnerMessage::getMessage)
                .toList();

        // when
        when(messageRepo.findAllById(anyList()))
                .thenReturn(messageList);

        // call the method
        assertThrows(IllegalArgumentException.class, () -> messageService.deleteMessages(messagesToDelete));

        // then
        verify(messageRepo, times(1)).findAllById(anyList());
    }

    @Test
    void getMessageById_WhenMessageIsFound() {
        // given
        Message message = ownerMessageList.get(0).getMessage();
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setId(message.getId());
        messageResponse.setText(message.getText());
        messageResponse.setSubject(message.getSubject());
        messageResponse.setSendDate(message.getSendDate());

        StaffShortResponse staff = new StaffShortResponse();
        staff.setId(currentStaff.getId());
        staff.setFirstName(currentStaff.getFirstName());
        staff.setLastName(currentStaff.getLastName());
        messageResponse.setStaff(staff);

        Long messageId = message.getId();

        // when
        when(messageRepo.findById(eq(messageId)))
                .thenReturn(Optional.of(message));
        when(messageMapper.messageToMessageResponse(eq(message)))
                .thenReturn(messageResponse);

        // call the method
        MessageResponse foundMessage = messageService.getMessageById(messageId);

        // then
        assertEquals(messageResponse, foundMessage);
        verify(messageRepo, times(1)).findById(eq(messageId));
        verify(messageMapper, times(1)).messageToMessageResponse(eq(message));
    }

    @Test
    void getMessageById_WhenMessageNotFound() {
        // given
        long messageId = 1L;

        // when
        when(messageRepo.findById(eq(messageId)))
                .thenReturn(Optional.empty());

        // call the method
        assertThrows(EntityNotFoundException.class, () -> messageService.getMessageById(messageId));

        // then
        verify(messageRepo, times(1)).findById(eq(messageId));
    }
}