package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.Invoice;
import com.example.myhouse24user.model.invoice.InvoiceResponse;
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

}
