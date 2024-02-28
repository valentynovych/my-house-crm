package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Invoice;
import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.entity.Tariff;
import com.example.myhouse24admin.model.invoices.InvoiceRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {
    @Mapping(target = "tariff", source = "tariff")
    @Mapping(target = "personalAccount", source = "personalAccount")
    @Mapping(target = "number", source = "number")
    @Mapping(target = "creationDate", expression = "java(convertStringToInstant(invoiceRequest.getCreationDate()))")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "deleted")
    @Mapping(target = "status", source = "invoiceRequest.status")
    Invoice invoiceRequestToInvoice(InvoiceRequest invoiceRequest,
                                    PersonalAccount personalAccount,
                                    Tariff tariff, String number);
    default Instant convertStringToInstant(String date) {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                LocalTime.now());
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant();
    }
}
