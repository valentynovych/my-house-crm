package com.example.myhouse24admin.model.statistic;

import java.math.BigDecimal;

public record BalanceStatistic(BigDecimal accountsBalanceArrears,
                               BigDecimal accountsBalanceOverpayments,
                               BigDecimal cashRegisterBalance
) {
}
