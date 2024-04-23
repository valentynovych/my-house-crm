package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.entity.OwnerMessage;
import com.example.myhouse24rest.mapper.OwnerMessageMapper;
import com.example.myhouse24rest.model.message.MessageResponse;
import com.example.myhouse24rest.repository.OwnerMessageRepo;
import com.example.myhouse24rest.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    private final OwnerMessageRepo ownerMessageRepo;
    private final OwnerMessageMapper ownerMessageMapper;
    private final Logger logger = LogManager.getLogger(MessageServiceImpl.class);

    public MessageServiceImpl(OwnerMessageRepo ownerMessageRepo, OwnerMessageMapper ownerMessageMapper) {
        this.ownerMessageRepo = ownerMessageRepo;
        this.ownerMessageMapper = ownerMessageMapper;
    }

    @Override
    public MessageResponse getMessageById(Long messageId, Principal principal) {
        logger.info("getMessageById() - Message by id: {} and ownerEmail: {}", messageId, principal.getName());
        OwnerMessage ownerMessage = findOwnerMessageByIdAndApartmentOwner_Email(messageId, principal.getName());
        MessageResponse messageResponse = ownerMessageMapper.ownerMessageToMessageResponse(ownerMessage);
        logger.info("getMessageById() - Message by id: {} and ownerEmail: {} found", messageId, principal.getName());
        return messageResponse;
    }

    @Override
    public void readMessageById(Long messageId, Principal principal) {
        logger.info("readMessageById() - Message by id: {} and ownerEmail: {}", messageId, principal.getName());
        OwnerMessage ownerMessage = findOwnerMessageByIdAndApartmentOwner_Email(messageId, principal.getName());
        ownerMessage.setRead(true);
        ownerMessageRepo.save(ownerMessage);
        logger.info("readMessageById() - Message by id: {} and ownerEmail: {} read", messageId, principal.getName());
    }

    @Override
    public Page<MessageResponse> getUnreadMessages(Principal principal, int page, int pageSize) {
        logger.info("getUnreadMessages() - Page: {} and pageSize: {} and ownerEmail: {}", page, pageSize, principal.getName());
        Pageable pageable = Pageable.ofSize(pageSize).withPage(page);
        Page<OwnerMessage> ownerMessages = ownerMessageRepo.findOwnerMessagesByApartmentOwner_EmailAndIsReadFalse(
                principal.getName(), pageable);
        Page<MessageResponse> messageResponses = convertPageOwnerMessageToPageMessageResponse(ownerMessages);
        logger.info("getUnreadMessages() - Page: {} and pageSize: {} and ownerEmail: {} converted", page, pageSize, principal.getName());
        return messageResponses;
    }

    @Override
    public Page<MessageResponse> getAllMessages(Principal principal, int page, int pageSize) {
        logger.info("getAllMessages() - Page: {} and pageSize: {} and ownerEmail: {}", page, pageSize, principal.getName());
        Pageable pageable = Pageable.ofSize(pageSize).withPage(page);
        Page<OwnerMessage> ownerMessages = ownerMessageRepo.findOwnerMessagesByApartmentOwner_Email(
                principal.getName(), pageable);
        Page<MessageResponse> messageResponses = convertPageOwnerMessageToPageMessageResponse(ownerMessages);
        logger.info("getAllMessages() - Page: {} and pageSize: {} and ownerEmail: {} converted", page, pageSize, principal.getName());
        return messageResponses;
    }

    private Page<MessageResponse> convertPageOwnerMessageToPageMessageResponse(Page<OwnerMessage> ownerMessages) {
        logger.info("convertPageOwnerMessageToPageMessageResponse() - Page: {}", ownerMessages);
        List<MessageResponse> messageResponses = ownerMessageMapper.ownerMessagesToMessageResponses(ownerMessages.getContent());
        Page<MessageResponse> messageResponsesPage =
                new PageImpl<>(messageResponses, ownerMessages.getPageable(), ownerMessages.getTotalElements());
        logger.info("convertPageOwnerMessageToPageMessageResponse() - Page: {} converted", ownerMessages);
        return messageResponsesPage;
    }

    private OwnerMessage findOwnerMessageByIdAndApartmentOwner_Email(Long messageId, String email) {
        logger.info("getMessageById() - Message by id: {} and ownerEmail: {}", messageId, email);
        Optional<OwnerMessage> ownerMessageByIdAndApartmentOwnerEmail =
                ownerMessageRepo.findOwnerMessageByMessageIdAndApartmentOwner_Email(messageId, email);
        OwnerMessage ownerMessage = ownerMessageByIdAndApartmentOwnerEmail.orElseThrow(() -> {
            logger.warn("getMessageById() - Message by id: {} and ownerEmail: {} not found", messageId, email);
            return new EntityNotFoundException("Message by id: " + messageId + " not found");
        });
        logger.info("getMessageById() - Message by id: {} and ownerEmail: {} found", messageId, email);
        return ownerMessage;
    }
}
