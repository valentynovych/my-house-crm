package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Invoice;
import com.example.myhouse24admin.entity.InvoiceItem;
import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.model.invoices.InvoiceItemRequest;
import com.example.myhouse24admin.model.invoices.InvoiceItemResponse;
import com.example.myhouse24admin.model.invoices.InvoiceResponse;
import com.example.myhouse24admin.model.invoices.XmlListInvoiceItemDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceItemMapper {
    @Mapping(ignore = true, target = "id")
    @Mapping(target = "service", source = "service")
    @Mapping(target = "invoice", source = "invoice")
    InvoiceItem invoiceItemRequestToInvoiceItem(InvoiceItemRequest invoiceItemRequest,
                                                Service service, Invoice invoice);
    List<InvoiceItemResponse> invoiceItemListToInvoiceItemResponseList(List<InvoiceItem> invoiceItems);
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "unitName", source = "service.unitOfMeasurement.name")
    InvoiceItemResponse invoiceItemToInvoiceItemResponse(InvoiceItem invoiceItem);
    List<XmlListInvoiceItemDto> invoiceItemListToXmlListInvoiceItemDtoList(List<InvoiceItem> invoiceItems);

    @Mapping(target = "service", source = "service.name")
    @Mapping(target = "unitOfMeasurement", source = "service.unitOfMeasurement.name")
    XmlListInvoiceItemDto invoiceItemToXmlListInvoiceItemDto(InvoiceItem invoiceItem);
}
