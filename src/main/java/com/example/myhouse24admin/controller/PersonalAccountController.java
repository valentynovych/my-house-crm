package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.apartments.personaAccount.PersonalAccountShortResponse;
import com.example.myhouse24admin.service.PersonalAccountService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/personal-accounts")
public class PersonalAccountController {

    private final PersonalAccountService personalAccountService;

    public PersonalAccountController(PersonalAccountService personalAccountService) {
        this.personalAccountService = personalAccountService;
    }

    @GetMapping("get-accounts-find-number")
    public @ResponseBody ResponseEntity<?> getAccountsFindByNumber(@RequestParam int page,
                                                                   @RequestParam int pageSize,
                                                                   @RequestParam String accountNumber) {
        Page<PersonalAccountShortResponse> accountShortResponses =
                personalAccountService.getAccountsFindByNumber(page, pageSize, accountNumber);
        return new ResponseEntity<>(accountShortResponses, HttpStatus.OK);
    }
}
