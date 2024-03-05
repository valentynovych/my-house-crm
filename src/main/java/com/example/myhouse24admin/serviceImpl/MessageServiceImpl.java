package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.Message;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.mapper.MessageMapper;
import com.example.myhouse24admin.model.messages.MessageSendRequest;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import com.example.myhouse24admin.repository.MessageRepo;
import com.example.myhouse24admin.service.ApartmentService;
import com.example.myhouse24admin.service.MailService;
import com.example.myhouse24admin.service.MessageService;
import com.example.myhouse24admin.service.StaffService;
import com.example.myhouse24admin.specification.ApartmentSpecification;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepo messageRepo;
    private final StaffService staffService;
    private final ApartmentService apartmentService;
    private final MessageMapper messageMapper;
    private final MailService mailService;
    private final ApartmentOwnerRepo apartmentOwnerRepo;

    public MessageServiceImpl(MessageRepo messageRepo,
                              StaffService staffService,
                              ApartmentService apartmentService,
                              MessageMapper messageMapper, MailService mailService, ApartmentOwnerRepo apartmentOwnerRepo) {
        this.messageRepo = messageRepo;
        this.staffService = staffService;
        this.apartmentService = apartmentService;
        this.messageMapper = messageMapper;
        this.mailService = mailService;
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public void sendNewMessage(MessageSendRequest messageSendRequest, HttpServletRequest request) {
        List<ApartmentOwner> apartmentOwnerForSendMessage = findApartmentOwnerForSendMessage(messageSendRequest);
        Staff currentStaff = staffService.getCurrentStaff();
        Message message = messageMapper.messageSendRequestToMessage(messageSendRequest, currentStaff, apartmentOwnerForSendMessage);
        for (ApartmentOwner owner : apartmentOwnerForSendMessage) {
            owner.getMessages().add(message);
            mailService.sendMessage(owner.getEmail(), messageSendRequest.getSubject(), messageSendRequest.getText(), currentStaff, request);
        }
        apartmentOwnerRepo.saveAll(apartmentOwnerForSendMessage);
    }

    private List<ApartmentOwner> findApartmentOwnerForSendMessage(MessageSendRequest messageSendRequest) {
        Pageable pageable = Pageable.ofSize(30);
        Map<String, String> searchParams = buildSearchParams(
                messageSendRequest.isForArrears(),
                messageSendRequest.getHouse(),
                messageSendRequest.getSection(),
                messageSendRequest.getFloor(),
                messageSendRequest.getApartment());
        ApartmentSpecification specification = new ApartmentSpecification(searchParams);
        List<Apartment> apartments = apartmentService.getAllApartmentsBy(pageable, new ArrayList<>(), specification);
        List<ApartmentOwner> apartmentOwners = apartments.stream()
                .map(Apartment::getOwner)
                .toList();
        return apartmentOwners;
    }

    private Map<String, String> buildSearchParams(boolean forArrears, Long houseId, Long sectionId, Long floorId, Long apartmentId) {
        Map<String, String> searchParams = new HashMap<>();
        if (forArrears) {
            searchParams.put("balance", "arrears");
        } else {
            putParamsToMap(searchParams, "house", houseId);
            putParamsToMap(searchParams, "section", sectionId);
            putParamsToMap(searchParams, "floor", floorId);
            putParamsToMap(searchParams, "apartment", apartmentId);
        }
        return searchParams;
    }

    private void putParamsToMap(Map<String, String> searchParams, String paramName, Long value) {
        if (value != null && value > 0) {
            searchParams.put(paramName, value.toString());
        }
    }

}
