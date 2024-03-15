package com.example.myhouse24user.controller;

import com.example.myhouse24user.entity.InvoiceStatus;
import com.example.myhouse24user.model.invoice.InvoiceResponse;
import com.example.myhouse24user.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/cabinet/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("")
    public ModelAndView getInvoicesPage() {
        return new ModelAndView("invoices/invoices");
    }
    @GetMapping("/get")
    public @ResponseBody Page<InvoiceResponse> getInvoices(@RequestParam Map<String, String> requestMap) {
        return invoiceService.getInvoiceResponses(requestMap);
    }
    @GetMapping("/get-statuses")
    public @ResponseBody InvoiceStatus[] getStatuses() {
        return InvoiceStatus.values();
    }
    @GetMapping("/{id}")
    public ModelAndView getInvoicesForApartmentPage() {
        return new ModelAndView("invoices/invoices");
    }
}
