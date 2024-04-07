package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.cashRegister.*;
import com.example.myhouse24admin.service.CashRegisterService;
import com.example.myhouse24admin.util.CashSheetTableExelGenerator;
import com.example.myhouse24admin.util.CashSheetViewExelGenerator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/cash-register")
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;
    private final MessageSource messageSource;

    public CashRegisterController(CashRegisterService cashRegisterService, MessageSource messageSource) {
        this.cashRegisterService = cashRegisterService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ModelAndView viewCashRegisterTable() {
        return new ModelAndView("cash-register/cash-register-table");
    }

    @GetMapping("add-income-sheet")
    public ModelAndView viewAddIncomeSheet(@RequestParam(required = false) String copyFrom) {
        return new ModelAndView("cash-register/add-income-sheet");
    }

    @GetMapping("edit-income-sheet/{sheetId}")
    public ModelAndView viewEditIncomeSheet(@PathVariable Long sheetId) {
        return new ModelAndView("cash-register/edit-income-sheet");
    }

    @GetMapping("add-expense-sheet")
    public ModelAndView viewAddExpenseSheet(@RequestParam(required = false) String copyFrom) {
        return new ModelAndView("cash-register/add-expense-sheet");
    }

    @GetMapping("edit-expense-sheet/{sheetId}")
    public ModelAndView viewEditExpenseSheet(@PathVariable Long sheetId) {
        return new ModelAndView("cash-register/edit-expense-sheet");
    }

    @GetMapping("view-sheet/{sheetId}")
    public ModelAndView viewSheet(@PathVariable Long sheetId) {
        return new ModelAndView("cash-register/view-cash-sheet");
    }

    @GetMapping("get-sheets")
    public @ResponseBody ResponseEntity<?> getSheets(@RequestParam int page,
                                                     @RequestParam int pageSize,
                                                     @RequestParam Map<String, String> searchParams) {
        Page<CashSheetTableResponse> responses = cashRegisterService.getSheets(page, pageSize, searchParams);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("add-income-sheet")
    public ResponseEntity<?> addNewIncomeSheet(@ModelAttribute("addRequest") @Valid CashSheetIncomeAddRequest addRequest) {
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
    public ResponseEntity<?> updateIncomeSheetById(@PathVariable Long sheetId,
                                                   @ModelAttribute("updateRequest") @Valid CashSheetIncomeUpdateRequest updateRequest) {
        cashRegisterService.updateSheetById(sheetId, updateRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("add-expense-sheet")
    public ResponseEntity<?> addNewExpenseSheet(@ModelAttribute("addRequest") @Valid CashSheetExpenseAddRequest addRequest) {
        cashRegisterService.addNewExpenseSheet(addRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("edit-expense-sheet/{sheetId}")
    public ResponseEntity<?> updateExpenseSheetById(@PathVariable Long sheetId,
                                                    @ModelAttribute("updateRequest") @Valid CashSheetExpenseUpdateRequest updateRequest) {
        cashRegisterService.updateSheetById(sheetId, updateRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("export-view-to-exel/{sheetId}")
    public ResponseEntity<?> exportToExcel(@PathVariable @Min(1) Long sheetId,
                                           HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        CashSheetResponse sheetResponse = cashRegisterService.getSheetById(sheetId);

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=CashSheet_" + sheetResponse.getSheetNumber() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        CashSheetViewExelGenerator generator =
                new CashSheetViewExelGenerator(sheetResponse, messageSource, LocaleContextHolder.getLocale());

        generator.generateExcelFile(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("export-table-to-exel")
    public ResponseEntity<?> exportTableToExcel(@RequestParam int page,
                                                @RequestParam int pageSize,
                                                @RequestParam Map<String, String> searchParams,
                                                HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        List<CashSheetTableResponse> responseList = cashRegisterService.getSheets(page, pageSize, searchParams).getContent();

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=CashSheets_" + date + ".xlsx";
        response.setHeader(headerKey, headerValue);

        CashSheetTableExelGenerator generator =
                new CashSheetTableExelGenerator(responseList, messageSource, LocaleContextHolder.getLocale());
        generator.generateExcelFile(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("delete-sheet/{sheetId}")
    public @ResponseBody ResponseEntity<?> deleteCashSheet(@PathVariable @Min(value = 1) Long sheetId) {
        String deletedStatus = cashRegisterService.deleteCashSheetById(sheetId);
        if (deletedStatus.equals("Success")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
    }
}
