package com.example.myhouse24admin.exception;

public class ServiceAlreadyUsedException extends RuntimeException {

    private final String serviceNames;

    public ServiceAlreadyUsedException(String message, String serviceNames) {
        super(message);
        this.serviceNames = serviceNames;
    }

    public String getServiceNames() {
        return serviceNames;
    }
}
