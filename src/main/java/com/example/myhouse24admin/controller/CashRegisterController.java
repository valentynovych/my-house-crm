package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.CashSheetType;
import com.example.myhouse24admin.service.CashRegisterService;
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
@RequestMapping("/admin/cash-register")
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;

    public CashRegisterController(CashRegisterService cashRegisterService) {
        this.cashRegisterService = cashRegisterService;
    }

    @GetMapping
    public ModelAndView viewCashRegisterTable() {
        return new ModelAndView("cash-register/cash-register-table");
    }

    @GetMapping("add-incomes")
    public ModelAndView viewAddIncomeSheet() {
        return new ModelAndView("cash-register/add-income-sheet");
    }

    @GetMapping("get-sheets")
    public @ResponseBody ResponseEntity<?> getSheets(@RequestParam int page,
                                                     @RequestParam int pageSize,
                                                     @RequestParam Map<String, String> searchParams) {
         //cashRegisterService.getSheets(page, page, searchParams);
        return new ResponseEntity<>("ds",HttpStatus.OK);
    }

}
