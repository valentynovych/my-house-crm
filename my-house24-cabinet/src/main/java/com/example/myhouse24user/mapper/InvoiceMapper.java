package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.Invoice;
import com.example.myhouse24user.model.invoice.*;
import com.example.myhouse24user.util.DateConverter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "creationDate", expression = "java(convertInstantToString(invoice.getCreationDate()))")
    InvoiceResponse invoiceToInvoiceResponse(Invoice invoice, BigDecimal totalPrice);
    default String convertInstantToString(Instant date) {
        return DateConverter.instantToString(date);
    }
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "invoiceItemResponses", source = "invoiceItemResponses")
    @Mapping(target = "number", source = "invoice.number")
    ViewInvoiceResponse invoiceToViewInvoiceResponse(Invoice invoice,
                                                     List<InvoiceItemResponse> invoiceItemResponses,
                                                     BigDecimal totalPrice);
    @Mapping(target = "invoiceItems", source = "xmlInvoiceItemsDto")
    @Mapping(target = "total", source = "totalPrice")
    @Mapping(target = "owner", expression = "java(invoice.getApartment().getOwner().getLastName()+\" \"+invoice.getApartment().getOwner().getFirstName()+\" \"+invoice.getApartment().getOwner().getMiddleName())")
    @Mapping(target = "personalAccount",
            expression = "java(formAccountNumber(invoice.getApartment().getPersonalAccount().getAccountNumber()))")
    @Mapping(target = "house", source = "invoice.apartment.house.name")
    @Mapping(target = "section", source = "invoice.apartment.section.name")
    @Mapping(target = "apartment", source = "invoice.apartment.apartmentNumber")
    @Mapping(target = "tariff", source = "invoice.apartment.tariff.name")
    @Mapping(target = "number", source = "invoice.number")
    @Mapping(target = "creationDate", expression = "java(convertInstantToString(invoice.getCreationDate()))")
    XmlInvoiceDto invoiceToXmlInvoiceDto(Invoice invoice, XmlInvoiceItemsDto xmlInvoiceItemsDto,
                                         BigDecimal totalPrice);
    default String formAccountNumber(Long longNumber){
        String number = "";
        for (int i = 0; i < 10 - longNumber.toString().length(); i++){
            number += "0";
        }
        number += longNumber;
        String accountNumber = number.substring(0, 5) + "-" + number.substring(5, 10);
        return accountNumber;
    }
}
