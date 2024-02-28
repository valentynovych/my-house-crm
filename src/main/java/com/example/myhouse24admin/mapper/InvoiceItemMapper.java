package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Invoice;
import com.example.myhouse24admin.entity.InvoiceItem;
import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.model.invoices.InvoiceItemRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceItemMapper {
    @Mapping(ignore = true, target = "id")
    @Mapping(target = "service", source = "service")
    @Mapping(target = "invoice", source = "invoice")
    InvoiceItem invoiceItemRequestToInvoiceItem(InvoiceItemRequest invoiceItemRequest,
                                                Service service, Invoice invoice);
}
