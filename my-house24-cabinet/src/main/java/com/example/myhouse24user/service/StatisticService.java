package com.example.myhouse24user.service;

import com.example.myhouse24user.model.statistic.GeneralOwnerStatistic;
import com.example.myhouse24user.model.statistic.StatisticDateItem;
import com.example.myhouse24user.model.statistic.StatisticItem;

import java.security.Principal;
import java.util.List;

public interface StatisticService {
    GeneralOwnerStatistic getGeneralStatistic(Long apartment, Principal principal);

    List<StatisticItem> getExpensePerMonthStatistic(Long apartment, Principal principal);

    List<StatisticItem> getExpensePerYearStatistic(Long apartment, Principal principal);

    List<StatisticDateItem> getExpensePerYearOnMonthStatistic(Long apartment, Principal principal);
}
