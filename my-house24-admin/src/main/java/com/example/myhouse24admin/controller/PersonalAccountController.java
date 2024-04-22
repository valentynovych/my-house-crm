package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.model.personalAccounts.*;
import com.example.myhouse24admin.service.PersonalAccountService;
import com.example.myhouse24admin.util.PersonalAccountExelGenerator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/personal-accounts")
public class PersonalAccountController {

    private final PersonalAccountService personalAccountService;
    private final MessageSource messageSource;

    public PersonalAccountController(PersonalAccountService personalAccountService, MessageSource messageSource) {
        this.personalAccountService = personalAccountService;
        this.messageSource = messageSource;
    }

    @GetMapping()
    public ModelAndView viewPersonalAccounts() {
        return new ModelAndView("personalAccounts/personal-accounts");
    }

    @GetMapping("add")
    public ModelAndView viewAddPersonalAccount() {
        return new ModelAndView("personalAccounts/add-personal-account");
    }

    @GetMapping("edit-account/{accountId}")
    public ModelAndView viewEditPersonalAccount() {
        return new ModelAndView("personalAccounts/edit-personal-account");
    }

    @GetMapping("view-account/{accountId}")
    public ModelAndView viewViewPersonalAccount(@PathVariable Long accountId) {
        return new ModelAndView("personalAccounts/view-personal-account");
    }

    @GetMapping("get-free-accounts-find-number")
    public @ResponseBody ResponseEntity<?> getAccountsFindByNumber(@RequestParam int page,
                                                                   @RequestParam int pageSize,
                                                                   @RequestParam String accountNumber) {
        Page<PersonalAccountShortResponse> accountShortResponses =
                personalAccountService.getAccountsFindByNumber(page, pageSize, accountNumber);
        return new ResponseEntity<>(accountShortResponses, HttpStatus.OK);
    }

    @GetMapping("get-personal-accounts")
    public @ResponseBody ResponseEntity<?> getPersonalAccounts(@RequestParam int page,
                                                               @RequestParam int pageSize,
                                                               @RequestParam Map<String, String> searchParams) {
        Page<PersonalAccountTableResponse> responses =
                personalAccountService.getPersonalAccounts(page, pageSize, searchParams);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("get-statuses")
    public @ResponseBody ResponseEntity<?> getPersonalAccountStatuses() {
        List<PersonalAccountStatus> responses = personalAccountService.getPersonalAccountStatuses();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("add")
    public ResponseEntity<?> addNewPersonalAccount(@ModelAttribute("request") @Valid PersonalAccountAddRequest request) {
        personalAccountService.addNewPersonalAccount(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-account/{accountId}")
    public @ResponseBody ResponseEntity<?> getPersonalAccount(@PathVariable Long accountId) {
        PersonalAccountResponse response = personalAccountService.getPersonalAccountById(accountId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("edit-account/{accountId}")
    public ResponseEntity<?> updatePersonalAccount(@PathVariable Long accountId,
                                                   @ModelAttribute("request") @Valid PersonalAccountUpdateRequest request) {
        personalAccountService.updatePersonalAccount(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-minimal-free-account-number")
    public ResponseEntity<?> getMinimalFreeAccountNumber() {
        String freeAccountNumber = personalAccountService.getMinimalFreeAccountNumber();
        return new ResponseEntity<>(freeAccountNumber, HttpStatus.OK);
    }

    @GetMapping("export-to-excel")
    public ResponseEntity<?> exportToExcel(@RequestParam int page,
                                           @RequestParam int pageSize,
                                           @RequestParam Map<String, String> searchParams,
                                           HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=PersonalAccounts_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<PersonalAccountTableResponse> accountTableResponses =
                personalAccountService.exportToExcel(page, pageSize, searchParams);
        PersonalAccountExelGenerator generator =
                new PersonalAccountExelGenerator(accountTableResponses, messageSource, LocaleContextHolder.getLocale());

        generator.generateExcelFile(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
