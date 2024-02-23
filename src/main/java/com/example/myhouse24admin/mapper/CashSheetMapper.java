package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.CashSheet;
import com.example.myhouse24admin.model.cashRegister.CashSheetIncomeAddRequest;
import com.example.myhouse24admin.model.cashRegister.CashSheetTableResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    @Mapping(target = "sheetType", constant = "INCOME")
    @Mapping(target = "personalAccount.id", source = "personalAccountId")
    @Mapping(target = "personalAccount.apartment.owner.id", source = "ownerId")
    @Mapping(target = "staff.id", source = "ownerId")
    @Mapping(target = "paymentItem.id", source = "paymentItemId")
    @Mapping(target = "sheetNumber", source = "sheetNumber")
    CashSheet cashSheetIncomeAddRequestToCashSheet(CashSheetIncomeAddRequest cashSheetIncomeAddRequest);

//    default Instant convertStringDateToInstant(String stringDate) {
//        LocalDate date = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
//        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
//    }
}
