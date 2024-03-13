package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.statistic.BalanceStatistic;
import com.example.myhouse24admin.model.statistic.IncomeExpenseStatistic;
import com.example.myhouse24admin.model.statistic.InvoicePaidArrearsStatistic;
import com.example.myhouse24admin.model.statistic.StatisticGeneralResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface StatisticService {
    BalanceStatistic getPersonalAccountsMetrics();

    StatisticGeneralResponse getGeneralStatistic() throws ExecutionException, InterruptedException;

    List<IncomeExpenseStatistic> getIncomeExpenseStatisticPerYear();

    List<InvoicePaidArrearsStatistic> getInvoicesPaidArrearsStatisticPerYear();
}
