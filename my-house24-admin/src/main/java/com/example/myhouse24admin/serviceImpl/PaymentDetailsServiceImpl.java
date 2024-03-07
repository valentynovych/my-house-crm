package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.PaymentDetail;
import com.example.myhouse24admin.mapper.PaymentDetailsMapper;
import com.example.myhouse24admin.model.paymentDetails.PaymentDetailsDto;
import com.example.myhouse24admin.model.paymentItem.PaymentItemDto;
import com.example.myhouse24admin.repository.PaymentDetailRepo;
import com.example.myhouse24admin.service.PaymentDetailsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentDetailsServiceImpl implements PaymentDetailsService {

    private final PaymentDetailRepo paymentDetailRepo;
    private final Logger log = LogManager.getLogger(PaymentItemServiceImpl.class);
    private final PaymentDetailsMapper mapper = Mappers.getMapper(PaymentDetailsMapper.class);

    public PaymentDetailsServiceImpl(PaymentDetailRepo paymentDetailRepo) {
        this.paymentDetailRepo = paymentDetailRepo;
    }

    @Override
    public PaymentDetailsDto getPaymentDetails() {
        log.info("getPaymentDetails() -> start");
        Optional<PaymentDetail> first = paymentDetailRepo.findAll().stream().findFirst();
        PaymentDetail paymentDetail = first.orElse(new PaymentDetail());
        PaymentDetailsDto dto = mapper.paymentDetailsToPaymentDetailsDto(paymentDetail);
        log.info("getPaymentDetails() -> exit");
        return dto;
    }

    @Override
    public void updatePaymentDetails(PaymentDetailsDto paymentDetailsDto) {
        log.info("updatePaymentDetails() -> start");
        PaymentDetail paymentDetail = mapper.paymentDetailsDtoToPaymentDetails(paymentDetailsDto);
        paymentDetailRepo.save(paymentDetail);
        log.info("updatePaymentDetails() -> success save, exit");
    }
}
