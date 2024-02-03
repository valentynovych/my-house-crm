package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.TariffItem;
import com.example.myhouse24admin.model.tariffs.TariffItemRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TariffItemMapper {

    @Mapping(target = "service.id", source = "serviceId")
    @Mapping(target = "currency", constant = "грн")
    TariffItem tariffItemRequestToTariffItem(TariffItemRequest tariffItemRequest);

    List<TariffItem> tariffItemRequestToTariffItem(List<TariffItemRequest> tariffItemRequests);


}
