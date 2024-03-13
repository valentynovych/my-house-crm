package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.InvoiceTemplate;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateRequest;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceTemplateMapper {
    @Mapping(target = "file", source = "savedFile")
    @Mapping(target = "name", source = "invoiceTemplateRequest.name")
    InvoiceTemplate invoiceTemplateRequestToInvoiceTemplate(InvoiceTemplateRequest invoiceTemplateRequest,
                                                            String savedFile);
    List<InvoiceTemplateResponse> invoiceTemplateListToInvoiceTemplateResponseList(List<InvoiceTemplate> invoiceTemplates);
    @Mapping(target = "isDefault", expression="java(invoiceTemplate.isDefault())")
    InvoiceTemplateResponse invoiceTemplateToInvoiceTemplateResponse(InvoiceTemplate invoiceTemplate);
}
