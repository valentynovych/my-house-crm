package com.example.myhouse24admin.model.statistic;

import java.math.BigDecimal;
import java.time.Instant;

public record InvoicePaidArrearsStatistic(Instant month, BigDecimal arrears, BigDecimal paidArrears) {
}
