package com.example.myhouse24user.model.statistic;

import java.math.BigDecimal;
import java.time.Instant;

public record StatisticDateItem(Instant month, BigDecimal amount) {
}
