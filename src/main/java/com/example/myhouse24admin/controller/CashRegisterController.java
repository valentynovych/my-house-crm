package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.cashRegister.CashSheetIncomeAddRequest;
import com.example.myhouse24admin.model.cashRegister.CashSheetIncomeUpdateRequest;
import com.example.myhouse24admin.model.cashRegister.CashSheetResponse;
import com.example.myhouse24admin.model.cashRegister.CashSheetTableResponse;
import com.example.myhouse24admin.service.CashRegisterService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @GetMapping("add-income-sheet")
    public ModelAndView viewAddIncomeSheet() {
        return new ModelAndView("cash-register/add-income-sheet");
    }

    @GetMapping("edit-income-sheet/{sheetId}")
    public ModelAndView viewEditIncomeSheet(@PathVariable Long sheetId) {
        return new ModelAndView("cash-register/edit-income-sheet");
    }

    @GetMapping("add-expense-sheet")
    public ModelAndView viewAddExpenseSheet() {
        return new ModelAndView("cash-register/add-income-sheet");
    }

    @GetMapping("get-sheets")
    public @ResponseBody ResponseEntity<?> getSheets(@RequestParam int page,
                                                     @RequestParam int pageSize,
                                                     @RequestParam Map<String, String> searchParams) {
        Page<CashSheetTableResponse> responses = cashRegisterService.getSheets(page, pageSize, searchParams);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("add-income-sheet")
    public ResponseEntity<?> addNewIncomeSheet(@ModelAttribute @Valid CashSheetIncomeAddRequest addRequest) {
        cashRegisterService.addNewIncomeSheet(addRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-next-sheet-number")
    public ResponseEntity<String> getNextSheetNumber() {
        String nextSheetNumber = cashRegisterService.getNextSheetNumber();
        return new ResponseEntity<>(nextSheetNumber, HttpStatus.OK);
    }

    @GetMapping("get-sheet/{sheetId}")
    public @ResponseBody ResponseEntity<?> getSheetById(@PathVariable @Min(1) Long sheetId) {
        CashSheetResponse sheetResponse = cashRegisterService.getSheetById(sheetId);
        return new ResponseEntity<>(sheetResponse, HttpStatus.OK);
    }

    @PostMapping("edit-income-sheet/{sheetId}")
    public ResponseEntity<?> updateSheetById(@PathVariable Long sheetId,
                                             @ModelAttribute @Valid CashSheetIncomeUpdateRequest updateRequest) {
        cashRegisterService.updateSheetById(sheetId, updateRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
