package com.example.myhouse24admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/cash-register")
public class CashRegisterController {

    @GetMapping
    public ModelAndView viewCashRegisterTable() {
        return new ModelAndView("cash-register/cash-register-table");
    }

    @GetMapping
    public ModelAndView viewAddIncomeSheet() {
        return new ModelAndView("cash-register/add-income-sheet");
    }

}
