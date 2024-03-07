package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.service.StatisticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/admin/statistic")
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping("")
    public ModelAndView getStatisticPage(){
        return new ModelAndView("statistic/statistic");
    }

    @GetMapping("get-accounts-statistic")
    public @ResponseBody ResponseEntity<?> getPersonalAccountsStatistic() {
        Map<String, String> personalAccountMetrics = statisticService.getPersonalAccountsMetrics();
        return new ResponseEntity<>(personalAccountMetrics, HttpStatus.OK);
    }


}
