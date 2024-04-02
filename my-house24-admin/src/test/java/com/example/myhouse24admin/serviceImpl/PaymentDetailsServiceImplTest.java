package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.PaymentDetail;
import com.example.myhouse24admin.mapper.PaymentDetailsMapper;
import com.example.myhouse24admin.model.paymentDetails.PaymentDetailsDto;
import com.example.myhouse24admin.repository.PaymentDetailRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentDetailsServiceImplTest {

    @Mock
    private PaymentDetailRepo paymentDetailRepo;
    @Mock
    private PaymentDetailsMapper mapper;
    @InjectMocks
    private PaymentDetailsServiceImpl paymentDetailsServiceImpl;

    private static PaymentDetail paymentDetail;

    @BeforeEach
    void setUp() {
        paymentDetail = new PaymentDetail();
        paymentDetail.setId(1L);
        paymentDetail.setCompanyName("companyName");
        paymentDetail.setCompanyDetails("companyDetails");
    }

    @Test
    void getPaymentDetails() {
        // given
        PaymentDetailsDto paymentDetailDto = new PaymentDetailsDto(
                paymentDetail.getId(),
                paymentDetail.getCompanyName(),
                paymentDetail.getCompanyDetails()
        );

        // when
        when(paymentDetailRepo.findAll())
                .thenReturn(Collections.singletonList(paymentDetail));
        when(mapper.paymentDetailsToPaymentDetailsDto(paymentDetail))
                .thenReturn(paymentDetailDto);

        // call the method
        var paymentDetailsDto = paymentDetailsServiceImpl.getPaymentDetails();

        // then
        assertNotNull(paymentDetailsDto);
        assertEquals(paymentDetailDto, paymentDetailsDto);

        verify(paymentDetailRepo).findAll();
        verify(mapper).paymentDetailsToPaymentDetailsDto(paymentDetail);
    }

    @Test
    void updatePaymentDetails() {
        // given
        PaymentDetailsDto paymentDetailDto = new PaymentDetailsDto(
                paymentDetail.getId(),
                paymentDetail.getCompanyName(),
                paymentDetail.getCompanyDetails()
        );

        // when
        when(mapper.paymentDetailsDtoToPaymentDetails(paymentDetailDto))
                .thenReturn(paymentDetail);

        // call the method
        paymentDetailsServiceImpl.updatePaymentDetails(paymentDetailDto);

        // then
        verify(paymentDetailRepo).save(paymentDetail);
    }
}