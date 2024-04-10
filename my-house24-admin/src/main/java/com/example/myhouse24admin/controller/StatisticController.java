package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.statistic.BalanceStatistic;
import com.example.myhouse24admin.model.statistic.IncomeExpenseStatistic;
import com.example.myhouse24admin.model.statistic.InvoicePaidArrearsStatistic;
import com.example.myhouse24admin.model.statistic.StatisticGeneralResponse;
import com.example.myhouse24admin.service.StatisticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/admin/statistic")
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping("")
    public ModelAndView getStatisticPage() {
        return new ModelAndView("statistic/statistic");
    }

    @GetMapping("get-accounts-statistic")
    public @ResponseBody ResponseEntity<?> getPersonalAccountsStatistic() {
        BalanceStatistic personalAccountsMetrics = statisticService.getPersonalAccountsMetrics();
        return new ResponseEntity<>(personalAccountsMetrics, HttpStatus.OK);
    }

    @GetMapping("get-general-statistic")
    public @ResponseBody ResponseEntity<?> getGeneralStatistic() {
        StatisticGeneralResponse generalStatistic;
        try {
            generalStatistic = statisticService.getGeneralStatistic();
            return new ResponseEntity<>(generalStatistic, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException exception) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("get-income-expense-statistic")
    public @ResponseBody ResponseEntity<?> getIncomeExpenseStatistic() {
//        try {
            List<IncomeExpenseStatistic> incomeExpenseStatisticPerYear = statisticService.getIncomeExpenseStatisticPerYear();
            return new ResponseEntity<>(incomeExpenseStatisticPerYear, HttpStatus.OK);
//        } catch (RuntimeException exception) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
    }

    @GetMapping("get-paid-arrears-statistic")
    public @ResponseBody ResponseEntity<?> getInvoicesPaidArrearsStatistic() {
        List<InvoicePaidArrearsStatistic> paidArrearsStatisticPerYear = statisticService.getInvoicesPaidArrearsStatisticPerYear();
        return new ResponseEntity<>(paidArrearsStatisticPerYear, HttpStatus.OK);
    }


}
