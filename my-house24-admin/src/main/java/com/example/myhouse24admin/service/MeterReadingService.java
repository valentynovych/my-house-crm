package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.meterReadings.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface MeterReadingService {
    void createMeterReading(MeterReadingRequest meterReadingRequest);
    String createNumber();
    Page<TableMeterReadingResponse> getMeterReadingResponsesForTable(Map<String, String> requestMap);
    Page<ApartmentMeterReadingResponse> getMeterReadingResponsesForTableInInvoice(int page, int pageSize,
                                                                              Long apartmentId);
    MeterReadingResponse getMeterReadingResponse(Long id);
    void updateMeterReading(Long id, MeterReadingRequest meterReadingRequest);
    Page<ApartmentMeterReadingResponse> getApartmentMeterReadingResponses(Long apartmentId,
                                                                          Map<String, String> requestMap);
    void deleteMeterReading(Long id);
    List<BigDecimal> getAmountOfConsumptions(Long[] serviceIds, Long apartmentId);
}
