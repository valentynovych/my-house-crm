package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.MeterReading;
import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.mapper.MeterReadingMapper;
import com.example.myhouse24admin.model.meterReadings.MeterReadingRequest;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.MeterReadingRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import com.example.myhouse24admin.service.MeterReadingService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
@org.springframework.stereotype.Service
public class MeterReadingServiceImpl implements MeterReadingService {
    private final MeterReadingRepo meterReadingRepo;
    private final ApartmentRepo apartmentRepo;
    private final ServicesRepo servicesRepo;
    private final MeterReadingMapper meterReadingMapper;
    private final Logger logger = LogManager.getLogger(MeterReadingServiceImpl.class);

    public MeterReadingServiceImpl(MeterReadingRepo meterReadingRepo,
                                   ApartmentRepo apartmentRepo,
                                   ServicesRepo servicesRepo,
                                   MeterReadingMapper meterReadingMapper) {
        this.meterReadingRepo = meterReadingRepo;
        this.apartmentRepo = apartmentRepo;
        this.servicesRepo = servicesRepo;
        this.meterReadingMapper = meterReadingMapper;
    }

    @Override
    public void createMeterReading(MeterReadingRequest meterReadingRequest) {
        logger.info("createMeterReading - Creating meter reading");
        Apartment apartment = apartmentRepo.findById(meterReadingRequest.apartmentId()).orElseThrow(() -> new EntityNotFoundException("Apartment was not found by id "+meterReadingRequest.apartmentId()));
        Service service = servicesRepo.findById(meterReadingRequest.serviceId()).orElseThrow(() -> new EntityNotFoundException("Service was not found by id "+meterReadingRequest.serviceId()));
        String number = createNumber();
        MeterReading meterReading = meterReadingMapper.meterReadingRequestToMeterReading(meterReadingRequest, apartment, service, number);
        meterReadingRepo.save(meterReading);
        logger.info("createMeterReading - Meter reading was created");
    }
    @Override
    public String createNumber(){
        Optional<MeterReading> meterReading = meterReadingRepo.findLast();
        return meterReading.map(reading -> formNumber(reading.getNumber())).orElse("0000000001");
    }
    private String formNumber(String lastNumber){
        Integer numberPart = Integer.valueOf(lastNumber);
        numberPart += 1;
        String newNumber = "";
        for (int i = 0; i < 5 - numberPart.toString().length(); i++) {
            newNumber += "0";
        }
        return newNumber + numberPart;
    }
}
