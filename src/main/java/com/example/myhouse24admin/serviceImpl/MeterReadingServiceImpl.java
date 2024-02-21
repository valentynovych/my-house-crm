package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.MeterReading;
import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.mapper.MeterReadingMapper;
import com.example.myhouse24admin.model.meterReadings.FilterRequest;
import com.example.myhouse24admin.model.meterReadings.MeterReadingRequest;
import com.example.myhouse24admin.model.meterReadings.MeterReadingResponse;
import com.example.myhouse24admin.model.meterReadings.TableMeterReadingResponse;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.MeterReadingRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import com.example.myhouse24admin.service.MeterReadingService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static com.example.myhouse24admin.specification.MeterReadingSpecification.*;

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

    @Override
    public Page<TableMeterReadingResponse> getMeterReadingResponsesForTable(int page, int pageSize, FilterRequest filterRequest) {
        logger.info("getMeterReadingResponseForTable - Getting meter reading responses for table, page: "+page+" pageSize: "+pageSize+" "+filterRequest.toString());
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<MeterReading> meterReadings = getFilteredReadings(filterRequest, pageable);
        List<TableMeterReadingResponse> tableMeterReadingResponses = meterReadingMapper.meterReadingListToTableMeterReadingResponseList(meterReadings.getContent());
        Page<TableMeterReadingResponse> tableMeterReadingResponsePage = new PageImpl<>(tableMeterReadingResponses, pageable, meterReadings.getTotalElements());
        logger.info("getMeterReadingResponseForTable - Meter reading responses was got");
        return tableMeterReadingResponsePage;
    }

    private Page<MeterReading> getFilteredReadings(FilterRequest filterRequest, Pageable pageable) {
        Specification<MeterReading> meterReadingSpecification = Specification.where(byDeleted().and(byMaxCreationDate()));
        if(filterRequest.sectionId() != null){
            meterReadingSpecification = meterReadingSpecification.and(bySectionId(filterRequest.sectionId()));
        }
        if(filterRequest.houseId() != null){
            meterReadingSpecification = meterReadingSpecification.and(byHouseId(filterRequest.houseId()));
        }
        if(filterRequest.serviceId() != null){
            meterReadingSpecification = meterReadingSpecification.and(byServiceId(filterRequest.serviceId()));
        }
        if(!filterRequest.apartment().isEmpty()){
            meterReadingSpecification = meterReadingSpecification.and(byApartmentNumber(Integer.valueOf(filterRequest.apartment())));
        }
        return meterReadingRepo.findAll(meterReadingSpecification, pageable);
    }

    @Override
    public MeterReadingResponse getMeterReadingResponse(Long id) {
        logger.info("getMeterReadingResponse - Getting meter reading response");
        MeterReading meterReading = meterReadingRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Meter reading was not found by id "+id));
        MeterReadingResponse meterReadingResponse = meterReadingMapper.meterReadingToMeterReadingResponse(meterReading);
        logger.info("getMeterReadingResponse - Meter reading response was got");
        return meterReadingResponse;
    }

    @Override
    public void updateMeterReading(Long id, MeterReadingRequest meterReadingRequest) {
        logger.info("getMeterReadingResponse - Updating meter reading by id "+id+" "+meterReadingRequest.toString());
        MeterReading meterReading = meterReadingRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Meter reading was not found by id "+id));
        Apartment apartment = apartmentRepo.findById(meterReadingRequest.apartmentId()).orElseThrow(() -> new EntityNotFoundException("Apartment was not found by id "+meterReadingRequest.apartmentId()));
        Service service = servicesRepo.findById(meterReadingRequest.serviceId()).orElseThrow(() -> new EntityNotFoundException("Service was not found by id "+meterReadingRequest.serviceId()));
        meterReadingMapper.updateMeterReading(meterReading, meterReadingRequest, apartment, service);
        meterReadingRepo.save(meterReading);
        logger.info("updateMeterReading - Meter reading was updated");
    }
}
