package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.CashSheet;
import com.example.myhouse24admin.model.cashRegister.CashSheetTableResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {PersonalAccountMapper.class,
                ApartmentOwnerMapper.class,
                PaymentItemMapper.class})
public interface CashSheetMapper {


    List<CashSheetTableResponse> sashSheetListToCashSheetTableResponseList(List<CashSheet> cashSheetList);
    @Mapping(target = "apartmentOwner", source = "personalAccount.apartment.owner")
    CashSheetTableResponse sashSheetToCashSheetTableResponse(CashSheet cashSheet);
}
