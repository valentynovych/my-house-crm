package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.meterReadings.MeterReadingRequest;

public interface MeterReadingService {
    void createMeterReading(MeterReadingRequest meterReadingRequest);
    String createNumber();
}
