package com.example.myhouse24admin.exception;

public class TariffAlreadyUsedException extends RuntimeException {
    private final String tariffName;

    public TariffAlreadyUsedException(String message, String tariffName) {
        super(message);
        this.tariffName = tariffName;
    }

    public String getTariffName() {
        return tariffName;
    }
}
