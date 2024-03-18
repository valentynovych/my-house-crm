package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.InvoiceItem;
import com.example.myhouse24user.model.invoice.InvoiceItemResponse;
import com.example.myhouse24user.model.invoice.XmlListInvoiceItemDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceItemMapper {
    List<InvoiceItemResponse> invoiceItemListToInvoiceItemResponseList(List<InvoiceItem> invoiceItems);
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "unitName", source = "service.unitOfMeasurement.name")
    InvoiceItemResponse invoiceItemToInvoiceItemResponse(InvoiceItem invoiceItem);
    List<XmlListInvoiceItemDto> invoiceItemListToXmlListInvoiceItemDtoList(List<InvoiceItem> invoiceItems);

    @Mapping(target = "service", source = "service.name")
    @Mapping(target = "unitOfMeasurement", source = "service.unitOfMeasurement.name")
    XmlListInvoiceItemDto invoiceItemToXmlListInvoiceItemDto(InvoiceItem invoiceItem);
}
