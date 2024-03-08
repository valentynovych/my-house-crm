package com.example.myhouse24user.entity;

public enum PaymentType {
    INCOME("Дохід"),
    EXPENSE("Витрати");

    public final String label;

    PaymentType(String label) {
        this.label = label;
    }
}
