package com.example.myhouse24admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StatisticController {
    @GetMapping("/statistic")
    public ModelAndView getStatisticPage(){
        return new ModelAndView("statistic/statistic.html");
    }
}
