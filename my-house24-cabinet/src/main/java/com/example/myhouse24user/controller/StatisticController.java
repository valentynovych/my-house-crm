package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.owner.ApartmentOwnerDetails;
import com.example.myhouse24user.model.statistic.GeneralOwnerStatistic;
import com.example.myhouse24user.model.statistic.StatisticDateItem;
import com.example.myhouse24user.model.statistic.StatisticItem;
import com.example.myhouse24user.service.StatisticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("cabinet/statistic")
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping
    public ModelAndView viewStatistic(Principal principal) {
        ApartmentOwnerDetails details = (ApartmentOwnerDetails) ((RememberMeAuthenticationToken) principal).getPrincipal();
        return new ModelAndView("redirect:/cabinet/statistic/" + details.getApartments().get(0).getId());
    }

    @GetMapping("/{apartmentId}")
    public ModelAndView viewStatisticByApartment(@PathVariable Long apartmentId) {
        return new ModelAndView("statistic/statistic");
    }

    @GetMapping("get-general-statistic")
    public @ResponseBody ResponseEntity<?> getGeneralStatisticByApartment(@RequestParam Long apartment, Principal principal) {
        GeneralOwnerStatistic generalOwnerStatistic = statisticService.getGeneralStatistic(apartment, principal);
        return new ResponseEntity<>(generalOwnerStatistic, HttpStatus.OK);
    }

    @GetMapping("get-expense-per-month")
    public @ResponseBody ResponseEntity<?> getExpensePerMonthByApartment(@RequestParam Long apartment, Principal principal) {
        List<StatisticItem> expensePerMonth = statisticService.getExpensePerMonthStatistic(apartment, principal);
        return new ResponseEntity<>(expensePerMonth, HttpStatus.OK);
    }

    @GetMapping("get-expense-per-year")
    public @ResponseBody ResponseEntity<?> getExpensePerYearByApartment(@RequestParam Long apartment, Principal principal) {
        List<StatisticItem> expensePerMonth = statisticService.getExpensePerYearStatistic(apartment, principal);
        return new ResponseEntity<>(expensePerMonth, HttpStatus.OK);
    }

    @GetMapping("get-expense-per-year-on-month")
    public @ResponseBody ResponseEntity<?> getExpensePerYearOnMonthByApartment(@RequestParam Long apartment, Principal principal) {
        List<StatisticDateItem> expensePerMonth = statisticService.getExpensePerYearOnMonthStatistic(apartment, principal);
        return new ResponseEntity<>(expensePerMonth, HttpStatus.OK);
    }
}
