package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.Invoice;
import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.entity.Tariff;
import com.example.myhouse24admin.model.invoices.InvoiceRequest;
import com.example.myhouse24admin.model.invoices.TableInvoiceResponse;
import com.example.myhouse24admin.util.DateConverter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {
    @Mapping(target = "apartment", source = "apartment")
    @Mapping(target = "number", source = "number")
    @Mapping(target = "creationDate", expression = "java(convertStringToInstant(invoiceRequest.getCreationDate()))")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "deleted")
    @Mapping(target = "status", source = "invoiceRequest.status")
    Invoice invoiceRequestToInvoice(InvoiceRequest invoiceRequest,
                                    Apartment apartment, String number);
    default Instant convertStringToInstant(String date) {
        return DateConverter.stringToInstant(date);
    }
    @Mapping(target = "apartment", expression = "java(invoice.getApartment().getApartmentNumber()+\", \"+invoice.getApartment().getHouse().getName())")
    @Mapping(target = "ownerFullName", expression = "java(invoice.getApartment().getOwner().getLastName()+\" \"+invoice.getApartment().getOwner().getMiddleName()+\" \"+invoice.getApartment().getOwner().getFirstName())")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "creationDate", expression = "java(convertInstantToString(invoice.getCreationDate()))")
    TableInvoiceResponse invoiceToTableInvoiceResponse(Invoice invoice, BigDecimal totalPrice);
    default String convertInstantToString(Instant date){
        return DateConverter.instantToString(date);
    }
}
