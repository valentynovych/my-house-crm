package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.mapper.MessageMapper;
import com.example.myhouse24admin.mapper.OwnerMessageMapper;
import com.example.myhouse24admin.model.messages.MessageResponse;
import com.example.myhouse24admin.model.messages.MessageSendRequest;
import com.example.myhouse24admin.model.messages.MessageTableResponse;
import com.example.myhouse24admin.repository.MessageRepo;
import com.example.myhouse24admin.repository.OwnerMessageRepo;
import com.example.myhouse24admin.service.ApartmentService;
import com.example.myhouse24admin.service.MailService;
import com.example.myhouse24admin.service.MessageService;
import com.example.myhouse24admin.service.StaffService;
import com.example.myhouse24admin.specification.ApartmentSpecification;
import com.example.myhouse24admin.specification.MessageSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepo messageRepo;
    private final StaffService staffService;
    private final ApartmentService apartmentService;
    private final MessageMapper messageMapper;
    private final MailService mailService;
    private final OwnerMessageMapper ownerMessageMapper;
    private final OwnerMessageRepo ownerMessageRepo;
    private final Logger logger = LogManager.getLogger(MessageServiceImpl.class);

    public MessageServiceImpl(MessageRepo messageRepo,
                              StaffService staffService,
                              ApartmentService apartmentService,
                              MessageMapper messageMapper,
                              MailService mailService,
                              OwnerMessageMapper ownerMessageMapper,
                              OwnerMessageRepo ownerMessageRepo) {
        this.messageRepo = messageRepo;
        this.staffService = staffService;
        this.apartmentService = apartmentService;
        this.messageMapper = messageMapper;
        this.mailService = mailService;
        this.ownerMessageMapper = ownerMessageMapper;
        this.ownerMessageRepo = ownerMessageRepo;
    }

    @Override
    public void sendNewMessage(MessageSendRequest messageSendRequest) {
        logger.info("sendNewMessage() -> send message: {}", messageSendRequest);
        List<ApartmentOwner> apartmentOwnerForSendMessage = findApartmentOwnerForSendMessage(messageSendRequest);
        Staff currentStaff = staffService.getCurrentStaff();
        Message message = messageMapper.messageSendRequestToMessage(messageSendRequest, currentStaff);
        sendNewMessageToOwners(message, apartmentOwnerForSendMessage);
        logger.info("sendNewMessage() -> success send messages to apartment owners");
    }

    @Override
    public Page<MessageTableResponse> getMessages(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("getMessages() -> start with params: searchParams: {}", searchParams);
        Page<Message> messagesBy = findMessagesBy(page, pageSize, searchParams);
        List<MessageTableResponse> responseList = messageMapper.messageListToMessageResponseTableList(messagesBy.getContent());
        Page<MessageTableResponse> responsePage = new PageImpl<>(responseList, messagesBy.getPageable(), messagesBy.getTotalElements());
        logger.info("getMessages() -> end return page elements: {}", responsePage.getNumberOfElements());
        return responsePage;
    }

    @Override
    public void deleteMessages(Long[] messagesToDelete) {
        logger.info("deleteMessages() -> start with ids: {}", Arrays.toString(messagesToDelete));
        List<Long> ids = Arrays.stream(messagesToDelete).toList();
        List<Message> allById = messageRepo.findAllById(ids);
        if (ids.size() != allById.size()) {
            logger.error("deleteMessages() -> Input array size not equals to find Messages array");
            throw new IllegalArgumentException("Input array size not equals to find Messages array");
        }
        deleteOwnerMessages(allById);
        messageRepo.deleteAll(allById);
        logger.info("deleteMessages() -> success delete messages, count: {}", allById.size());
    }

    private void deleteOwnerMessages(List<Message> allById) {
        logger.info("deleteOwnerMessages() -> start with count: {}", allById.size());
        List<OwnerMessage> allByMessageIn = ownerMessageRepo.findAllByMessageIn(allById);
        ownerMessageRepo.deleteAll(allByMessageIn);
        logger.info("deleteOwnerMessages() -> success delete owner messages");
    }

    @Override
    public MessageResponse getMessageById(Long messageId) {
        logger.info("getMessageById() -> start with id: {}", messageId);
        Message message = findMessageById(messageId);
        MessageResponse messageResponse = messageMapper.messageToMessageResponse(message);
        logger.info("getMessageById() -> end return message with id: {}", messageResponse.getId());
        return messageResponse;
    }

    private void sendNewMessageToOwners(Message message, List<ApartmentOwner> apartmentOwnerForSendMessage) {
        logger.info("sendNewMessage() -> send message: {}", message);
        for (ApartmentOwner owner : apartmentOwnerForSendMessage) {
            logger.info("sendNewMessage() -> send message to: {}", owner.getEmail());
            mailService.sendMessage(owner.getEmail(), message.getSubject(),
                    message.getText(), message.getStaff());
        }
        logger.info("sendNewMessage() -> success send message to all apartment owners, count: {}",
                apartmentOwnerForSendMessage.size());
        saveOwnerMessages(apartmentOwnerForSendMessage, message);
    }

    private void saveOwnerMessages(List<ApartmentOwner> apartmentOwnerForSendMessage, Message message) {
        logger.info("saveOwnerMessages() -> start with count: {}", apartmentOwnerForSendMessage.size());
        List<OwnerMessage> ownerMessages = new ArrayList<>();
        for (ApartmentOwner owner : apartmentOwnerForSendMessage) {
            ownerMessages.add(
                    ownerMessageMapper.createOwnerMessageFromMessageAndApartmentOwner(message, owner));
        }
        ownerMessageRepo.saveAll(ownerMessages);
        logger.info("saveOwnerMessages() -> success save owner messages, count: {}", ownerMessages.size());
    }

    private Message findMessageById(Long messageId) {
        logger.info("findMessageById() -> start with id: {}", messageId);
        Optional<Message> byId = messageRepo.findById(messageId);
        Message message = byId.orElseThrow(() -> {
            logger.error("findMessageById() -> Message with id: {} not found", messageId);
            return new EntityNotFoundException(String.format("Message with id: %s not found", messageId));
        });
        logger.info("findMessageById() -> end return message with id: {}", message.getId());
        return message;
    }

    private Page<Message> findMessagesBy(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("findMessagesBy() -> start with params: searchParams: {}", searchParams);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "sendDate"));
        MessageSpecification specification = new MessageSpecification(searchParams);
        Page<Message> all = messageRepo.findAll(specification, pageable);
        logger.info("findMessagesBy() -> end return page elements: {}", all.getNumberOfElements());
        return all;
    }

    private List<ApartmentOwner> findApartmentOwnerForSendMessage(MessageSendRequest messageSendRequest) {
        logger.info("findApartmentOwnerForSendMessage() -> start with request: messageSendRequest: {}", messageSendRequest);
        Pageable pageable = Pageable.ofSize(30);
        Map<String, String> searchParams = buildSearchParams(
                messageSendRequest.isForArrears(),
                messageSendRequest.getHouse(),
                messageSendRequest.getSection(),
                messageSendRequest.getFloor(),
                messageSendRequest.getApartment());
        ApartmentSpecification specification = new ApartmentSpecification(searchParams);
        List<Apartment> apartments = apartmentService.getAllApartmentsBy(pageable, new ArrayList<>(), specification);
        logger.info("findApartmentOwnerForSendMessage() -> end return apartment owners: {}", apartments.size());
        List<ApartmentOwner> apartmentOwners = apartments.stream()
                .map(Apartment::getOwner)
                .toList();
        logger.info("findApartmentOwnerForSendMessage() -> end return apartment owners: {}", apartmentOwners.size());
        return apartmentOwners;
    }

    private Map<String, String> buildSearchParams(boolean forArrears, Long houseId, Long sectionId, Long floorId, Long apartmentId) {
        logger.info("buildSearchParams() -> start with params: " +
                        "forArrears: {}, houseId: {}, sectionId: {}, floorId: {}, apartmentId: {}",
                forArrears, houseId, sectionId, floorId, apartmentId);
        Map<String, String> searchParams = new HashMap<>();
        if (forArrears) {
            logger.info("buildSearchParams() -> for arrears, ignore params: house, section, floor, apartment");
            searchParams.put("balance", "arrears");
        } else {
            logger.info("buildSearchParams() -> not for arrears, set params: house, section, floor, apartment");
            putParamsToMap(searchParams, "house", houseId);
            putParamsToMap(searchParams, "section", sectionId);
            putParamsToMap(searchParams, "floor", floorId);
            putParamsToMap(searchParams, "apartment", apartmentId);
        }
        logger.info("buildSearchParams() -> end return params: {}", searchParams);
        return searchParams;
    }

    private void putParamsToMap(Map<String, String> searchParams, String paramName, Long value) {
        logger.info("putParamsToMap() -> start with params: paramName: {}, value: {}", paramName, value);
        if (value != null && value > 0) {
            logger.info("putParamsToMap() -> set param: paramName: {}, value: {}", paramName, value);
            searchParams.put(paramName, value.toString());
        }
        logger.info("putParamsToMap() -> end");
    }

}
