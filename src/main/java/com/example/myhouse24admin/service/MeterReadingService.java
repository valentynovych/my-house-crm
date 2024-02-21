package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.meterReadings.FilterRequest;
import com.example.myhouse24admin.model.meterReadings.MeterReadingRequest;
import com.example.myhouse24admin.model.meterReadings.MeterReadingResponse;
import com.example.myhouse24admin.model.meterReadings.TableMeterReadingResponse;
import org.springframework.data.domain.Page;

public interface MeterReadingService {
    void createMeterReading(MeterReadingRequest meterReadingRequest);
    String createNumber();
    Page<TableMeterReadingResponse> getMeterReadingResponsesForTable(int page, int pageSize,
                                                                     FilterRequest filterRequest);
    MeterReadingResponse getMeterReadingResponse(Long id);
    void updateMeterReading(Long id, MeterReadingRequest meterReadingRequest);
}
