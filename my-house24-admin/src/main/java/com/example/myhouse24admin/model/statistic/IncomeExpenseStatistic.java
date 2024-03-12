package com.example.myhouse24admin.model.statistic;

import java.math.BigDecimal;
import java.time.Instant;

public record IncomeExpenseStatistic(
        Instant month,
        BigDecimal allIncomes,
        BigDecimal allExpenses) {
}
