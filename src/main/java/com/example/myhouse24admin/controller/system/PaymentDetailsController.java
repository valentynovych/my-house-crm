package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.model.paymentDetails.PaymentDetailsDto;
import com.example.myhouse24admin.service.PaymentDetailsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("admin/system-settings/payment-details")
public class PaymentDetailsController {

    private final PaymentDetailsService paymentDetailsService;

    public PaymentDetailsController(PaymentDetailsService paymentDetailsService) {
        this.paymentDetailsService = paymentDetailsService;
    }

    @GetMapping
    public ModelAndView viewPaymentDetails() {
        return new ModelAndView("system/payment.details/payment-details");
    }

    @GetMapping("get-details")
    @ResponseBody
    public ResponseEntity<?> getPaymentDetails() {
        PaymentDetailsDto paymentDetails = paymentDetailsService.getPaymentDetails();
        return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
    }

    @PostMapping("update-details")
    @ResponseBody
    public ResponseEntity<?> updatePaymentDetails(@Valid @ModelAttribute PaymentDetailsDto paymentDetailsDto) {
        paymentDetailsService.updatePaymentDetails(paymentDetailsDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
