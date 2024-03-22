package com.example.myhouse24rest.entity;

public enum PaymentType {
    INCOME("Дохід"),
    EXPENSE("Витрати");

    public final String label;

    PaymentType(String label) {
        this.label = label;
    }
}
