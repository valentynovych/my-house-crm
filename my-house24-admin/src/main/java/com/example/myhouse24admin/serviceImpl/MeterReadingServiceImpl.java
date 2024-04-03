package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.MeterReading;
import com.example.myhouse24admin.entity.MeterReadingStatus;
import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.mapper.MeterReadingMapper;
import com.example.myhouse24admin.model.meterReadings.*;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.MeterReadingRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import com.example.myhouse24admin.service.MeterReadingService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        int numberPart = Integer.parseInt(lastNumber);
        return StringUtils.leftPad(Integer.toString(numberPart + 1), 10, "0");
    }

    @Override
    public Page<TableMeterReadingResponse> getMeterReadingResponsesForTable(Map<String, String> requestMap) {
        logger.info("getMeterReadingResponseForTable - Getting meter reading responses for table, "+requestMap.toString());
        Pageable pageable = PageRequest.of(Integer.parseInt(requestMap.get("page")), Integer.parseInt(requestMap.get("pageSize")));
        Page<MeterReading> meterReadings = getFilteredReadings(requestMap, pageable);
        List<TableMeterReadingResponse> tableMeterReadingResponses = meterReadingMapper.meterReadingListToTableMeterReadingResponseList(meterReadings.getContent());
        Page<TableMeterReadingResponse> tableMeterReadingResponsePage = new PageImpl<>(tableMeterReadingResponses, pageable, meterReadings.getTotalElements());
        logger.info("getMeterReadingResponseForTable - Meter reading responses was got");
        return tableMeterReadingResponsePage;
    }

    private Page<MeterReading> getFilteredReadings(Map<String, String> requestMap, Pageable pageable) {
        Specification<MeterReading> meterReadingSpecification = Specification.where(byDeleted().and(byMaxCreationDate()));
        meterReadingSpecification = formSpecification(meterReadingSpecification, requestMap);
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
    public Page<ApartmentMeterReadingResponse> getApartmentMeterReadingResponses(Long apartmentId, Map<String, String> requestMap) {
        logger.info("getApartmentMeterReadingResponses - Getting apartment meter reading responses for apartment with id "+apartmentId+" "+requestMap.toString());
        Pageable pageable = PageRequest.of(Integer.parseInt(requestMap.get("page")), Integer.parseInt(requestMap.get("pageSize")));
        Page<MeterReading> meterReadings = getFilteredReadingsForApartment(apartmentId,requestMap, pageable);
        Page<ApartmentMeterReadingResponse> meterReadingResponsePage = createApartmentMeterReadingResponsePage(meterReadings, pageable);
        logger.info("getApartmentMeterReadingResponses - Apartment meter reading responses were got");
        return meterReadingResponsePage;
    }

    private Page<MeterReading> getFilteredReadingsForApartment(Long apartmentId,
                                                               Map<String, String> requestMap,
                                                               Pageable pageable) {
        Specification<MeterReading> meterReadingSpecification = Specification
                .where(byDeleted().and(byApartmentId(apartmentId)));
        meterReadingSpecification = formSpecification(meterReadingSpecification, requestMap);
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
        Page<ApartmentMeterReadingResponse> meterReadingResponsePage = createApartmentMeterReadingResponsePage(meterReadings, pageable);
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

    private Page<ApartmentMeterReadingResponse> createApartmentMeterReadingResponsePage(Page<MeterReading> meterReadings, Pageable pageable){
        List<ApartmentMeterReadingResponse> meterReadingResponses = meterReadingMapper.meterReadingListToApartmentMeterReadingResponseList(meterReadings.getContent());
        return new PageImpl<>(meterReadingResponses, pageable, meterReadings.getTotalElements());
    }
    @Override
    public List<BigDecimal> getAmountOfConsumptions(Long[] serviceIds, Long apartmentId) {
        logger.info("getAmountOfConsumptions - Getting amount of consumptions for service ids "+serviceIds.toString());
        List<BigDecimal> amounts = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "creationDate"));
        for(Long serviceId: serviceIds){
            Page<MeterReading> meterReadings = meterReadingRepo.findAll(byServiceId(serviceId).
                            and(byApartmentId(apartmentId)).and(byDeleted()), pageable);
            List<MeterReading> content = meterReadings.getContent();
            if(content.size() == 2) {
                BigDecimal amount = content.get(0).getReadings().subtract(content.get(1).getReadings());
                amounts.add(amount);
            } else if(content.size() == 1){
                amounts.add(content.get(0).getReadings());
            } else {
                amounts.add(BigDecimal.valueOf(0));
            }
        }
        logger.info("getAmountOfConsumptions - Amount of consumptions were got");
        return amounts;
    }

    private Specification<MeterReading> formSpecification(Specification<MeterReading> meterReadingSpecification, Map<String, String> requestMap){
        if(requestMap.containsKey("number") &&
                !requestMap.get("number").isEmpty()){
            meterReadingSpecification = meterReadingSpecification.and(byNumber(requestMap.get("number")));
        }
        if(requestMap.containsKey("status") &&
                !requestMap.get("status").isEmpty()){
            meterReadingSpecification = meterReadingSpecification.and(byStatus(MeterReadingStatus.valueOf(requestMap.get("status"))));
        }
        if(requestMap.containsKey("creationDate") &&
                !requestMap.get("creationDate").isEmpty()){
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(requestMap.get("creationDate"), DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    LocalTime.MIDNIGHT);
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant dateFrom = zonedDateTime.toInstant();
            Instant dateTo = zonedDateTime.toInstant().plus(1, ChronoUnit.DAYS);
            meterReadingSpecification = meterReadingSpecification.and(byCreationDateGreaterThen(dateFrom)).and(byCreationDateLessThan(dateTo));
        }
        if(!requestMap.get("sectionId").isEmpty()){
            meterReadingSpecification = meterReadingSpecification.and(bySectionId(Long.valueOf(requestMap.get("sectionId"))));
        }
        if(!requestMap.get("houseId").isEmpty()){
            meterReadingSpecification = meterReadingSpecification.and(byHouseId(Long.valueOf(requestMap.get("houseId"))));
        }
        if(!requestMap.get("serviceId").isEmpty()){
            meterReadingSpecification = meterReadingSpecification.and(byServiceId(Long.valueOf(requestMap.get("serviceId"))));
        }
        if(!requestMap.get("apartment").isEmpty()){
            meterReadingSpecification = meterReadingSpecification.and(byApartmentNumber(requestMap.get("apartment")));
        }
        return meterReadingSpecification;
    }
}
