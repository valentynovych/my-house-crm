package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.PaymentItem;
import com.example.myhouse24admin.entity.PaymentType;
import com.example.myhouse24admin.mapper.PaymentItemMapper;
import com.example.myhouse24admin.model.paymentItem.PaymentItemDto;
import com.example.myhouse24admin.repository.PaymentItemRepo;
import com.example.myhouse24admin.service.PaymentItemService;
import com.example.myhouse24admin.specification.PaymentItemSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentItemServiceImpl implements PaymentItemService {

    private final PaymentItemRepo paymentItemRepo;
    private final Logger log = LogManager.getLogger(PaymentItemServiceImpl.class);
    private final PaymentItemMapper mapper = Mappers.getMapper(PaymentItemMapper.class);

    public PaymentItemServiceImpl(PaymentItemRepo paymentItemRepo) {
        this.paymentItemRepo = paymentItemRepo;
    }

    @Override
    public Page<PaymentItemDto> getPaymentItems(int page, int pageSize, Map<String, String> searchParams) {
        log.info("getPaymentItems() -> start");
        Pageable pageable = PageRequest.of(page, pageSize);
        PaymentItemSpecification specification = new PaymentItemSpecification(searchParams);
        Page<PaymentItem> paymentItemPage = paymentItemRepo.findAll(specification, pageable);
        List<PaymentItemDto> paymentItemDtos =
                mapper.paymentItemListToPaymentItemDtoList(paymentItemPage.getContent());
        Page<PaymentItemDto> paymentItemDtoPage =
                new PageImpl<>(paymentItemDtos, pageable, paymentItemPage.getTotalElements());
        log.info("getPaymentItems() -> exit, return list size: " + paymentItemDtos.size());
        return paymentItemDtoPage;
    }

    @Override
    public PaymentItemDto getItemById(Long itemId) {
        log.info("getItemById() -> start, with id: " + itemId);
        Optional<PaymentItem> itemOptional = paymentItemRepo.findById(itemId);
        PaymentItem paymentItem = itemOptional.orElseThrow(() ->
                new EntityNotFoundException(String.format("Статтю за id: %s не знайденно", itemId)));
        PaymentItemDto paymentItemDto = mapper.paymentItemToPaymentItemDto(paymentItem);
        log.info("getItemById() -> exit");
        return paymentItemDto;
    }

    @Override
    public void addItem(PaymentItemDto paymentItemDto) {
        log.info("addItem() -> start, with PaymentItem: " + paymentItemDto);
        PaymentItem paymentItem = mapper.paymentItemDtoToPaymentItem(paymentItemDto);
        PaymentItem save = paymentItemRepo.save(paymentItem);
        log.info("addItem() -> success save, with id: " + save.getId());
    }

    @Override
    public void editItemById(Long itemId, PaymentItemDto paymentItemDto) {
        log.info("editItemById() -> start, with id: " + itemId);
         if (!paymentItemRepo.existsById(itemId)) {
             log.error("PaymentItem not found");
             throw new EntityNotFoundException(String.format("Статтю за id: %s не знайденно", itemId));
         }
        PaymentItem paymentItem = mapper.paymentItemDtoToPaymentItem(paymentItemDto);
        paymentItemRepo.save(paymentItem);
        log.info("editItemById() -> success edit, exit");
    }

    @Override
    public boolean deleteItemById(Long itemId) {
        return false;
    }

    @Override
    public Map<String, String> getItemTypes() {
        Map<String, String> paymentTypes = new HashMap<>();
        for (PaymentType paymentType : Arrays.stream(PaymentType.values()).toList()) {
            paymentTypes.put(paymentType.name(), paymentType.label);
        }
        return paymentTypes;
    }
}
