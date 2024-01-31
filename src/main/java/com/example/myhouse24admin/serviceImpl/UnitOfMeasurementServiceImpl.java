package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.UnitOfMeasurement;
import com.example.myhouse24admin.mapper.UnitOfMeasurementMapper;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDto;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDtoListWrap;
import com.example.myhouse24admin.repository.UnitOfMeasurementRepo;
import com.example.myhouse24admin.service.UnitOfMeasurementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitOfMeasurementServiceImpl implements UnitOfMeasurementService {

    private final UnitOfMeasurementRepo unitOfMeasurementRepo;
    private final Logger logger = LogManager.getLogger(UnitOfMeasurementServiceImpl.class);
    private final UnitOfMeasurementMapper mapper = Mappers.getMapper(UnitOfMeasurementMapper.class);

    public UnitOfMeasurementServiceImpl(UnitOfMeasurementRepo unitOfMeasurementRepo) {
        this.unitOfMeasurementRepo = unitOfMeasurementRepo;
    }

    @Override
    public List<UnitOfMeasurementDto> getAllMeasurementUnits() {
        logger.info("getAllMeasurementUnits() -> start");
        List<UnitOfMeasurement> all = unitOfMeasurementRepo.findAll(
                Specification.where((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("deleted"), false)));
        List<UnitOfMeasurementDto> dtoList = mapper.unitOfMeasurementListToUnitOfMeasurementDtoList(all);
        logger.info("getAllMeasurementUnits() -> exit, return list size: {}", dtoList.size());
        return dtoList;
    }

    @Override
    public void updateMeasurementUnist(UnitOfMeasurementDtoListWrap measurementListWrap) {
        logger.info("updateMeasurementUnist() -> start");
        List<UnitOfMeasurement> unitOfMeasurements =
                mapper.unitOfMeasurementListDtoToUnitOfMeasurementList(measurementListWrap.getUnitOfMeasurements());
        unitOfMeasurementRepo.saveAll(unitOfMeasurements);
        logger.info("updateMeasurementUnist() -> success update unitOfMeasurements");

        if (measurementListWrap.getUnitsToDelete() != null
                && !measurementListWrap.getUnitsToDelete().isEmpty()) {
            List<Long> unitsToDelete = measurementListWrap.getUnitsToDelete();
            //TODO add throw exception when service already use in services
            logger.info("updateMeasurementUnist() -> To have unitsToDelete: " + unitsToDelete.toString());
            List<UnitOfMeasurement> allById = unitOfMeasurementRepo.findAllById(unitsToDelete);
            allById.forEach(unitOfMeasurement -> unitOfMeasurement.setDeleted(true));
            unitOfMeasurementRepo.saveAll(allById);
            logger.info("updateMeasurementUnist() -> Success deleting MeasurementUnist ");
        }
    }
}
