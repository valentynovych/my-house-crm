package com.example.myhouse24user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("cabinet/statistic")
public class StatisticController {

    @GetMapping
    public ModelAndView viewStatistic() {
        return new ModelAndView("statistic/statistic");
    }
}
