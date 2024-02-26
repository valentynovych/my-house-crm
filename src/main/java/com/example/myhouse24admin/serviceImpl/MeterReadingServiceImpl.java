package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.MeterReading;
import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.mapper.MeterReadingMapper;
import com.example.myhouse24admin.model.meterReadings.*;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.MeterReadingRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import com.example.myhouse24admin.service.MeterReadingService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
        for (int i = 0; i < 10 - numberPart.toString().length(); i++) {
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
            meterReadingSpecification = meterReadingSpecification.and(byApartmentNumber(filterRequest.apartment()));
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

    @Override
    public Page<ApartmentMeterReadingResponse> getApartmentMeterReadingResponses(Long apartmentId, int page, int pageSize, ApartmentFilterRequest apartmentFilterRequest) {
        logger.info("getApartmentMeterReadingResponses - Getting apartment meter reading responses for apartment with id "+apartmentId+" "+apartmentFilterRequest.toString());
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<MeterReading> meterReadings = getFilteredReadingsForApartment(apartmentId,apartmentFilterRequest, pageable);
        List<ApartmentMeterReadingResponse> meterReadingResponses = meterReadingMapper.meterReadingListToApartmentMeterReadingResponseList(meterReadings.getContent());
        Page<ApartmentMeterReadingResponse> meterReadingResponsePage = new PageImpl<>(meterReadingResponses, pageable, meterReadings.getTotalElements());
        logger.info("getApartmentMeterReadingResponses - Apartment meter reading responses were got");
        return meterReadingResponsePage;
    }

    private Page<MeterReading> getFilteredReadingsForApartment(Long apartmentId,
                                                               ApartmentFilterRequest apartmentFilterRequest,
                                                               Pageable pageable) {
        Specification<MeterReading> meterReadingSpecification = Specification.where(byDeleted().and(byApartmentId(apartmentId)));
        if(!apartmentFilterRequest.number().isEmpty()){
            meterReadingSpecification = meterReadingSpecification.and(byNumber(apartmentFilterRequest.number()));
        }
        if(apartmentFilterRequest.status() != null){
            meterReadingSpecification = meterReadingSpecification.and(byStatus(apartmentFilterRequest.status()));
        }
        if(!apartmentFilterRequest.creationDate().isEmpty()){
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(apartmentFilterRequest.creationDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    LocalTime.MIDNIGHT);
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant dateFrom = zonedDateTime.toInstant();
            Instant dateTo = zonedDateTime.toInstant().plus(1, ChronoUnit.DAYS);
            meterReadingSpecification = meterReadingSpecification.and(byCreationDateGreaterThen(dateFrom)).and(byCreationDateLessThan(dateTo));
        }
        if(apartmentFilterRequest.sectionId() != null){
            meterReadingSpecification = meterReadingSpecification.and(bySectionId(apartmentFilterRequest.sectionId()));
        }
        if(apartmentFilterRequest.houseId() != null){
            meterReadingSpecification = meterReadingSpecification.and(byHouseId(apartmentFilterRequest.houseId()));
        }
        if(apartmentFilterRequest.serviceId() != null){
            meterReadingSpecification = meterReadingSpecification.and(byServiceId(apartmentFilterRequest.serviceId()));
        }
        if(!apartmentFilterRequest.apartment().isEmpty()){
            meterReadingSpecification = meterReadingSpecification.and(byApartmentNumber(apartmentFilterRequest.apartment()));
        }
        return meterReadingRepo.findAll(meterReadingSpecification,pageable);
    }

    @Override
    public void deleteMeterReading(Long id) {
        logger.info("deleteMeterReading - Deleting meter reading by id "+id);
        MeterReading meterReading = meterReadingRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Meter reading was not found by id "+id));
        meterReading.setDeleted(true);
        meterReadingRepo.save(meterReading);
        logger.info("deleteMeterReading - Meter reading was deleted");
    }

    @Override
    public Page<ApartmentMeterReadingResponse> getMeterReadingResponsesForTableInInvoice(int page, int pageSize, Long apartmentId) {
        logger.info("getMeterReadingResponsesForTableInInvoice - Getting meter reading responses for table in invoice, page: "+page+" pageSize: "+pageSize+" apartmentId: "+apartmentId);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("creationDate").descending());
        Page<MeterReading> meterReadings = getFilteredReadingsForTableInInvoice(apartmentId,pageable);
        List<ApartmentMeterReadingResponse> meterReadingResponses = meterReadingMapper.meterReadingListToApartmentMeterReadingResponseList(meterReadings.getContent());
        Page<ApartmentMeterReadingResponse> meterReadingResponsePage = new PageImpl<>(meterReadingResponses, pageable, meterReadings.getTotalElements());
        logger.info("getMeterReadingResponsesForTableInInvoice - Meter reading responses were got");
        return meterReadingResponsePage;
    }

    private Page<MeterReading> getFilteredReadingsForTableInInvoice(Long apartmentId, Pageable pageable) {
        Specification<MeterReading> meterReadingSpecification = Specification.where(byDeleted());
        if(apartmentId != null){
            meterReadingSpecification = meterReadingSpecification.and(byApartmentId(apartmentId));
        }
        return meterReadingRepo.findAll(meterReadingSpecification, pageable);
    }
}
