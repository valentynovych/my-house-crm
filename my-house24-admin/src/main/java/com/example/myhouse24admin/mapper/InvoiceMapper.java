package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.Invoice;
import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.entity.Tariff;
import com.example.myhouse24admin.model.invoices.*;
import com.example.myhouse24admin.util.DateConverter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "creationDate", expression = "java(convertInstantToString(invoice.getCreationDate()))")
    @Mapping(target = "sectionNameResponse.id", source = "invoice.apartment.section.id")
    @Mapping(target = "sectionNameResponse.name", source = "invoice.apartment.section.name")
    @Mapping(target = "houseNameResponse.id", source = "invoice.apartment.house.id")
    @Mapping(target = "houseNameResponse.name", source = "invoice.apartment.house.name")
    @Mapping(target = "apartmentNumberResponse.id", source = "invoice.apartment.id")
    @Mapping(target = "apartmentNumberResponse.apartmentNumber", source = "invoice.apartment.apartmentNumber")
    @Mapping(target = "status", source = "invoice.status")
    @Mapping(target = "ownerResponse.accountNumber", source = "invoice.apartment.personalAccount.accountNumber")
    @Mapping(target = "ownerResponse.ownerFullName", expression = "java(apartment.getOwner().getLastName()+\" \"+apartment.getOwner().getMiddleName()+\" \"+apartment.getOwner().getFirstName())")
    @Mapping(target = "ownerResponse.ownerPhoneNumber", source = "invoice.apartment.owner.phoneNumber")
    @Mapping(target = "ownerResponse.tariffId", source = "invoice.apartment.tariff.id")
    @Mapping(target = "ownerResponse.tariffName", source = "invoice.apartment.tariff.name")
    @Mapping(target = "itemResponses", source = "itemResponses")
    @Mapping(target = "number", source = "invoice.number")
    @Mapping(target = "processed", expression = "java(invoice.isProcessed())")
    InvoiceResponse invoiceToInvoiceResponse(Invoice invoice, BigDecimal totalPrice, List<InvoiceItemResponse> itemResponses);
    default String convertInstantToString(Instant date){
        return DateConverter.instantToString(date);
    }
    @Mapping(target = "apartment", source = "apartment")
    @Mapping(target = "creationDate", expression = "java(convertStringToInstant(invoiceRequest.getCreationDate()))")
    @Mapping(target = "status", source = "invoiceRequest.status")
    @Mapping(ignore = true, target = "id")
    void updateInvoice(@MappingTarget Invoice invoice, InvoiceRequest invoiceRequest,
                       Apartment apartment);
    @Mapping(target = "number", source = "invoice.number")
    @Mapping(target = "creationDate", expression = "java(convertInstantToString(invoice.getCreationDate()))")
    @Mapping(target = "processed", expression = "java(invoice.isProcessed())")
    @Mapping(target = "invoiceStatus", source = "invoice.status")
    @Mapping(target = "owner", expression = "java(invoice.getApartment().getOwner().getLastName()+\" \"+invoice.getApartment().getOwner().getMiddleName()+\" \"+invoice.getApartment().getOwner().getFirstName())")
    @Mapping(target = "accountNumber", source = "invoice.apartment.personalAccount.accountNumber")
    @Mapping(target = "phoneNumber", source = "invoice.apartment.owner.phoneNumber")
    @Mapping(target = "house", source = "invoice.apartment.house.name")
    @Mapping(target = "apartment", source = "invoice.apartment.apartmentNumber")
    @Mapping(target = "section", source = "invoice.apartment.section.name")
    @Mapping(target = "tariff", source = "invoice.apartment.tariff.name")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "itemResponses", source = "itemResponses")
    ViewInvoiceResponse invoiceToViewInvoiceResponse(Invoice invoice, List<InvoiceItemResponse> itemResponses, BigDecimal totalPrice);
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
