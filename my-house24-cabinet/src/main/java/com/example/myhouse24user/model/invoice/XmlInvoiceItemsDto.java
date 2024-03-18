package com.example.myhouse24user.model.invoice;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
@XmlRootElement(name = "invoiceItems")
public class XmlInvoiceItemsDto {
    private List<XmlListInvoiceItemDto> invoiceItems;

    public XmlInvoiceItemsDto() {
    }

    public XmlInvoiceItemsDto(List<XmlListInvoiceItemDto> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    @XmlElement(name = "invoiceItem")
    public List<XmlListInvoiceItemDto> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<XmlListInvoiceItemDto> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }
}
