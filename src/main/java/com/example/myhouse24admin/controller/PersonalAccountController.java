package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.model.apartments.personaAccount.PersonalAccountShortResponse;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountTableResponse;
import com.example.myhouse24admin.service.PersonalAccountService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/personal-accounts")
public class PersonalAccountController {

    private final PersonalAccountService personalAccountService;

    public PersonalAccountController(PersonalAccountService personalAccountService) {
        this.personalAccountService = personalAccountService;
    }

    @GetMapping()
    public ModelAndView viewPersonalAccounts() {
        return new ModelAndView("personalAccounts/personal-accounts");
    }

    @GetMapping("add")
    public ModelAndView addNewPersonalAccount() {
        return new ModelAndView("personalAccounts/add-personal-account");
    }

    @GetMapping("get-accounts-find-number")
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
}
