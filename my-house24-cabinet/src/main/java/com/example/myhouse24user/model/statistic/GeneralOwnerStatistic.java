package com.example.myhouse24user.model.statistic;

import java.math.BigDecimal;

public record GeneralOwnerStatistic(
        BigDecimal currentBalance,
        String personalAccountNumber,
        BigDecimal expenseOnLastMonth) {
}
