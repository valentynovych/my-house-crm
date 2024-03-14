package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateListRequest;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateResponse;
import com.example.myhouse24admin.service.InvoiceTemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequestMapping("/admin/invoices/templates-settings")
public class InvoiceTemplateController {
    private final InvoiceTemplateService invoiceTemplateService;

    public InvoiceTemplateController(InvoiceTemplateService invoiceTemplateService) {
        this.invoiceTemplateService = invoiceTemplateService;
    }

    @GetMapping("")
    public ModelAndView getTemplatesSettingsPage() {
        return new ModelAndView("invoices/templates-settings");
    }
    @GetMapping("/get")
    public @ResponseBody List<InvoiceTemplateResponse> getInvoiceTemplates() {
        return invoiceTemplateService.getInvoiceTemplatesResponses();
    }
    @PostMapping("")
    public @ResponseBody ResponseEntity<?> updateInvoiceTemplates(@Valid @ModelAttribute InvoiceTemplateListRequest invoiceTemplateListRequest) {
        invoiceTemplateService.updateTemplates(invoiceTemplateListRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/set-default/{id}")
    public @ResponseBody ResponseEntity<?> setDefaultInvoice(@PathVariable Long id) {
        invoiceTemplateService.setDefaultInvoice(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping ("/download-template/{fileName}")
    public @ResponseBody ResponseEntity<byte[]> downloadTemplate(@PathVariable String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        byte[] file = invoiceTemplateService.getTemplateFile(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="+ URLEncoder.encode(fileName, "UTF-8"))
                .body(file);
    }
}
