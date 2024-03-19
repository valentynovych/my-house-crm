package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.Tariff;
import com.example.myhouse24user.mapper.TariffMapper;
import com.example.myhouse24user.model.tariff.TariffResponse;
import com.example.myhouse24user.repository.ApartmentRepo;
import com.example.myhouse24user.service.TariffService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TariffServiceImpl implements TariffService {

    private final ApartmentRepo apartmentRepo;
    private final TariffMapper tariffMapper;
    private final Logger logger = LogManager.getLogger(TariffServiceImpl.class);

    public TariffServiceImpl(ApartmentRepo apartmentRepo, TariffMapper tariffMapper) {
        this.apartmentRepo = apartmentRepo;
        this.tariffMapper = tariffMapper;
    }

    @Override
    public TariffResponse getApartmentTariff(Long apartmentId) {
        logger.info("getApartmentTariff() -> start, with apartmentId: {}", apartmentId);
        Tariff tariff = findApartmentById(apartmentId).getTariff();
        TariffResponse tariffResponse = tariffMapper.tariffToTariffResponse(tariff);
        logger.info("getApartmentTariff() -> end, with tariffResponse: {}", tariffResponse);
        return tariffResponse;
    }

    private Apartment findApartmentById(Long apartmentId) {
        logger.info("findApartmentById() -> start, with apartmentId: {}", apartmentId);
        Optional<Apartment> one = apartmentRepo.findById(apartmentId);
        Apartment apartment = one.orElseThrow(() -> {
            logger.error("Apartment with id " + apartmentId + " not found");
            return new EntityNotFoundException("Apartment with id " + apartmentId + " not found");
        });
        logger.info("findApartmentById() -> end, with apartmentNumber: {}", apartment.getApartmentNumber());
        return apartment;
    }
}
