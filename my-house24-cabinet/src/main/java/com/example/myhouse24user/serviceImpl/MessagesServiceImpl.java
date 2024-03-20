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
        Page<OwnerMessage> messagesByOwnerEmail = getMessagesByOwnerEmail(name, pageable, search);
        List<OwnerMessageResponse> messageResponses =
                ownerMessageMapper.ownerMessageListToMessageResponseList(messagesByOwnerEmail.getContent());
        Page<OwnerMessageResponse> responsePage = new PageImpl<>(messageResponses, pageable, messagesByOwnerEmail.getTotalElements());
        logger.info("getApartmentOwnerMessages() -> success, with name: {}", name);
        return responsePage;
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

    private OwnerMessage findMessageByOwnerEmailAndId(String name, Long messageId) {
        logger.info("findMessageByOwnerEmailAndId() -> start, with id: {}, ownerEmail: {}", messageId, name);
        OwnerMessageSpecification specification = new OwnerMessageSpecification(getMapSearchParams(name, messageId));
        Optional<OwnerMessage> one = ownerMessageRepo.findOne(specification);
        OwnerMessage message = one.orElseThrow(() -> {
            logger.error("Message with id: {}, ownerEmail: {} - not found", messageId, name);
            return new EntityNotFoundException(String.format("Message with id: %s, ownerEmail: %s - not found",
                    messageId, name));
        });
        logger.info("findMessageByOwnerEmailAndId() -> end, return message: {}", message);
        return message;
    }

    private Page<OwnerMessage> getMessagesByOwnerEmail(String ownerEmail, Pageable pageable, String search) {
        logger.info("getMessagesByOwnerEmail() -> start, with email: {}", ownerEmail);
        OwnerMessageSpecification specification = new OwnerMessageSpecification(getMapSearchParams(ownerEmail, search));
        Page<OwnerMessage> all = ownerMessageRepo.findAll(specification, pageable);
        logger.info("getMessagesByOwnerEmail() -> end, return messages size: {}", all.getNumberOfElements());
        return all;
    }

    private Map<String, String> getMapSearchParams(String ownerEmail, String search) {
        logger.info("getMapSearchParams() -> start, with email: {}, search: {}", ownerEmail, search);
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("ownerEmail", ownerEmail);
        if (search != null && !search.isEmpty()) {
            searchParams.put("search", search);
        }
        logger.info("getMapSearchParams() -> end, return searchParams: {}", searchParams);
        return searchParams;
    }

    private Map<String, String> getMapSearchParams(String ownerEmail, Long messageId) {
        logger.info("getMapSearchParams() -> start, with email: {}, messageId: {}", ownerEmail, messageId);
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("ownerEmail", ownerEmail);
        if (messageId != null) {
            searchParams.put("id", messageId.toString());
        }
        logger.info("getMapSearchParams() -> end, return searchParams: {}", searchParams);
        return searchParams;
    }
}
