package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.Message;
import com.example.myhouse24user.mapper.MessageMapper;
import com.example.myhouse24user.model.messages.MessageResponse;
import com.example.myhouse24user.repository.MessageRepo;
import com.example.myhouse24user.service.ApartmentOwnerService;
import com.example.myhouse24user.service.MessagesService;
import com.example.myhouse24user.specification.MessageSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;

@Service
public class MessagesServiceImpl implements MessagesService {

    private final MessageMapper messageMapper;
    private final MessageRepo messageRepo;
    private final ApartmentOwnerService apartmentOwnerService;
    private final Logger logger = LogManager.getLogger(MessagesServiceImpl.class);

    public MessagesServiceImpl(MessageMapper messageMapper, MessageRepo messageRepo, ApartmentOwnerService apartmentOwnerService) {
        this.messageRepo = messageRepo;
        this.messageMapper = messageMapper;
        this.apartmentOwnerService = apartmentOwnerService;
    }

    @Override
    public Page<MessageResponse> getApartmentOwnerMessages(String name, int page, int pageSize, String search) {
        logger.info("getApartmentOwnerMessages() -> start, with name: {}", name);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "sendDate"));
        Page<Message> messagesByOwnerEmail = getMessagesByOwnerEmail(name, pageable, search);
        List<MessageResponse> messageResponses =
                messageMapper.messageListToMessageResponseList(messagesByOwnerEmail.getContent());
        Page<MessageResponse> responsePage = new PageImpl<>(messageResponses, pageable, messagesByOwnerEmail.getTotalElements());
        logger.info("getApartmentOwnerMessages() -> success, with name: {}", name);
        return responsePage;
    }

    @Override
    public MessageResponse getMessageById(String name, Long messageId) {
        logger.info("getMessageById() -> start, with id: {}", messageId);
        Message message = findMessageByOwnerEmailAndId(name, messageId);
        MessageResponse messageResponse = messageMapper.messageToMessageResponse(message);
        logger.info("getMessageById() -> success, with id: {}", messageId);
        return messageResponse;
    }

    @Transactional
    @Override
    public void deleteMessages(Principal principal, Long[] messageIdsToDelete) {
        logger.info("deleteMessages() -> start with ids: {}", Arrays.toString(messageIdsToDelete));
        List<Long> ids = Arrays.stream(messageIdsToDelete).toList();
        List<Message> allById = messageRepo.findAllById(ids);
        if (ids.size() != allById.size()) {
            logger.error("deleteMessages() -> Input array size not equals to find Messages array");
            throw new IllegalArgumentException("Input array size not equals to find Messages array");
        }
        ApartmentOwner apartmentOwner = apartmentOwnerService.findApartmentOwnerByEmail(principal.getName());
        List<Message> messagesToDelete = apartmentOwner.getMessages().stream()
                .filter(message -> ids.contains(message.getId())).toList();
        apartmentOwner.getMessages().removeAll(messagesToDelete);
        logger.info("deleteMessages() -> success delete messages, count: {}", allById.size());
    }

    private Message findMessageByOwnerEmailAndId(String name, Long messageId) {
        logger.info("findMessageByOwnerEmailAndId() -> start, with id: {}, ownerEmail: {}", messageId, name);
        MessageSpecification specification = new MessageSpecification(getMapSearchParams(name, messageId));
        Optional<Message> one = messageRepo.findOne(specification);
        Message message = one.orElseThrow(() -> {
            logger.error("Message with id: {}, ownerEmail: {} - not found", messageId, name);
            return new EntityNotFoundException(String.format("Message with id: %s, ownerEmail: %s - not found",
                    messageId, name));
        });
        logger.info("findMessageByOwnerEmailAndId() -> end, return message: {}", message);
        return message;
    }

    private Page<Message> getMessagesByOwnerEmail(String ownerEmail, Pageable pageable, String search) {
        logger.info("getMessagesByOwnerEmail() -> start, with email: {}", ownerEmail);
        MessageSpecification specification = new MessageSpecification(getMapSearchParams(ownerEmail, search));
        Page<Message> all = messageRepo.findAll(specification, pageable);
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
