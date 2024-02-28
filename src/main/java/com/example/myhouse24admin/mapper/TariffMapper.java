package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Tariff;
import com.example.myhouse24admin.entity.TariffItem;
import com.example.myhouse24admin.model.invoices.TariffItemResponse;
import com.example.myhouse24admin.model.invoices.TariffNameResponse;
import com.example.myhouse24admin.model.tariffs.TariffRequest;
import com.example.myhouse24admin.model.tariffs.TariffResponse;
import com.example.myhouse24admin.model.tariffs.TariffShortResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {TariffItemMapper.class})
public interface TariffMapper {

    @Mapping(target = "lastModify", expression = "java(java.time.Instant.now())")
    Tariff tariffRequestToTariff(TariffRequest tariffRequest);

    List<TariffResponse> tariffListToTariffResponseList(List<Tariff> tariffs);

    TariffResponse tariffToTariffResponse(Tariff tariff);

    @Mapping(target = "lastModify", expression = "java(java.time.Instant.now())")
    void updateTariffFromTariffRequest(@MappingTarget Tariff tariff, TariffRequest tariffRequest);

    TariffShortResponse tariffToTariffShortResponse(Tariff tariff);
    List<TariffNameResponse> tariffListToTariffNameResponseList(List<Tariff> tariffs);
    List<TariffItemResponse> tariffItemListToTariffItemResponse(List<TariffItem> tariffItems);
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "unitOfMeasurement", source = "service.unitOfMeasurement.name")
    TariffItemResponse tariffItemToTariffItemResponse(TariffItem tariffItem);
}
