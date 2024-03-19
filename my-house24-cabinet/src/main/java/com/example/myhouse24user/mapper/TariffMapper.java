package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.Tariff;
import com.example.myhouse24user.entity.TariffItem;
import com.example.myhouse24user.model.tariff.TariffItemResponse;
import com.example.myhouse24user.model.tariff.TariffResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TariffMapper {

    TariffResponse tariffToTariffResponse(Tariff tariff);

    default TariffItemResponse tariffItemToTariffItemResponse(TariffItem tariffItem) {
        Long id = tariffItem.getId();
        String serviceName = tariffItem.getService().getName();
        String unitOfMeasurementName = tariffItem.getService().getUnitOfMeasurement().getName();
        BigDecimal servicePrice = tariffItem.getServicePrice();
        TariffItemResponse tariffItemResponse = new TariffItemResponse(id, serviceName, unitOfMeasurementName, servicePrice);
        return tariffItemResponse;

    }
}
