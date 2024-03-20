package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.OwnerMessage;
import com.example.myhouse24user.mapper.OwnerMessageMapper;
import com.example.myhouse24user.model.messages.OwnerMessageResponse;
import com.example.myhouse24user.repository.OwnerMessageRepo;
import com.example.myhouse24user.service.MessagesService;
import com.example.myhouse24user.specification.OwnerMessageSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
public class MessagesServiceImpl implements MessagesService {

    private final OwnerMessageRepo ownerMessageRepo;
    private final OwnerMessageMapper ownerMessageMapper;
    private final Logger logger = LogManager.getLogger(MessagesServiceImpl.class);

    public MessagesServiceImpl(OwnerMessageRepo ownerMessageRepo,
                               OwnerMessageMapper ownerMessageMapper) {
        this.ownerMessageRepo = ownerMessageRepo;
        this.ownerMessageMapper = ownerMessageMapper;
    }

    @Override
    public Page<OwnerMessageResponse> getApartmentOwnerMessages(String name, int page, int pageSize, String search) {
        logger.info("getApartmentOwnerMessages() -> start, with name: {}", name);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "message.sendDate"));
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("ownerEmail", name);
        if (search != null && !search.isEmpty()) {
            searchParams.put("search", search);
        }
        Page<OwnerMessage> messagesByOwnerEmail = findMessagesBySearchParams(
                searchParams, pageable);
        Page<OwnerMessageResponse> ownerMessageResponses =
                convertMessagePageToMessageResponsePage(messagesByOwnerEmail, pageable);
        logger.info("getApartmentOwnerMessages() -> success, with name: {}", name);
        return ownerMessageResponses;
    }

    @Override
    public OwnerMessageResponse getMessageById(String name, Long messageId) {
        logger.info("getMessageById() -> start, with id: {}", messageId);
        OwnerMessage message = findMessageByOwnerEmailAndId(name, messageId);
        OwnerMessageResponse messageResponse = ownerMessageMapper.ownerMessageToMessageResponse(message);
        logger.info("getMessageById() -> success, with id: {}", messageId);
        return messageResponse;
    }

    @Override
    public void deleteMessages(Principal principal, Long[] messageIdsToDelete) {
        logger.info("deleteMessages() -> start with ids: {}", Arrays.toString(messageIdsToDelete));
        List<Long> ids = Arrays.stream(messageIdsToDelete).toList();
        List<OwnerMessage> allById = ownerMessageRepo.findAllByIdIsInAndApartmentOwner_Email(ids, principal.getName());
        if (ids.size() != allById.size()) {
            logger.error("deleteMessages() -> Input array size not equals to find Messages array");
            throw new IllegalArgumentException("Input array size not equals to find Messages array");
        }
        allById.forEach(message -> message.setDeleted(true));
        ownerMessageRepo.deleteAll(allById);
        logger.info("deleteMessages() -> success delete messages, count: {}", allById.size());
    }

    @Override
    public void readMessage(String ownerEmail, Long messageId) {
        logger.info("readMessage() -> start, with id: {}", messageId);
        OwnerMessage message = findMessageByOwnerEmailAndId(ownerEmail, messageId);
        message.setRead(true);
        ownerMessageRepo.save(message);
        logger.info("readMessage() -> success, with id: {}", messageId);
    }

    @Override
    public Page<OwnerMessageResponse> getUnreadMessages(String name, int page, int pageSize) {
        logger.info("getUnreadMessages() -> start, with name: {}", name);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "message.sendDate"));
        Page<OwnerMessage> messagesByOwnerEmail = findMessagesBySearchParams(
                Map.of("ownerEmail", name, "read", "false"), pageable);
        Page<OwnerMessageResponse> ownerMessageResponses =
                convertMessagePageToMessageResponsePage(messagesByOwnerEmail, pageable);
        logger.info("getUnreadMessages() -> success, with name: {}, messages count: {}",
                name, messagesByOwnerEmail.getTotalElements());
        return ownerMessageResponses;
    }

    @Override
    public void readAllMessage(String ownerEmail, List<Long> idsToMarkAsRead) {
        logger.info("readAllMessage() -> start, with ids: {}", idsToMarkAsRead);
        List<OwnerMessage> allById = ownerMessageRepo.findAllByIdIsInAndApartmentOwner_Email(idsToMarkAsRead, ownerEmail);
        if (idsToMarkAsRead.size() != allById.size()) {
            logger.error("readAllMessage() -> Input array size not equals to find Messages array");
            throw new IllegalArgumentException("Input array size not equals to find Messages array");
        }
        allById.forEach(message -> message.setRead(true));
        ownerMessageRepo.saveAll(allById);
        logger.info("readAllMessage() -> success, with ids: {}", idsToMarkAsRead);
    }

    private OwnerMessage findMessageByOwnerEmailAndId(String name, Long messageId) {
        logger.info("findMessageByOwnerEmailAndId() -> start, with id: {}, ownerEmail: {}", messageId, name);
        OwnerMessageSpecification specification = new OwnerMessageSpecification(
                Map.of("id", String.valueOf(messageId), "ownerEmail", name));
        Optional<OwnerMessage> one = ownerMessageRepo.findOne(specification);
        OwnerMessage message = one.orElseThrow(() -> {
            logger.error("Message with id: {}, ownerEmail: {} - not found", messageId, name);
            return new EntityNotFoundException(String.format("Message with id: %s, ownerEmail: %s - not found",
                    messageId, name));
        });
        logger.info("findMessageByOwnerEmailAndId() -> end, return message: {}", message);
        return message;
    }

    private Page<OwnerMessage> findMessagesBySearchParams(Map<String, String> searchParams, Pageable pageable) {
        logger.info("getMessagesByOwnerEmail() -> start, with searchParams: {}", searchParams);
        OwnerMessageSpecification specification = new OwnerMessageSpecification(searchParams);
        Page<OwnerMessage> all = ownerMessageRepo.findAll(specification, pageable);
        logger.info("getMessagesByOwnerEmail() -> end, return messages size: {}", all.getNumberOfElements());
        return all;
    }

    private Page<OwnerMessageResponse> convertMessagePageToMessageResponsePage(Page<OwnerMessage> ownerMessagePage, Pageable pageable) {
        logger.info("convertMessagePageToMessageResponsePage() -> start convert OwnerMessagePage to MessageResponsePage");
        List<OwnerMessageResponse> messageResponses =
                ownerMessageMapper.ownerMessageListToMessageResponseList(ownerMessagePage.getContent());
        Page<OwnerMessageResponse> responsePage = new PageImpl<>(messageResponses, pageable, ownerMessagePage.getTotalElements());
        logger.info("convertMessagePageToMessageResponsePage() -> success convert OwnerMessagePage to MessageResponsePage");
        return responsePage;
    }
}
