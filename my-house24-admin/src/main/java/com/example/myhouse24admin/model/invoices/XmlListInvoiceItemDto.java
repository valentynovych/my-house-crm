package com.example.myhouse24admin.model.invoices;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
@XmlRootElement(name = "invoiceItem")
public class XmlListInvoiceItemDto {
    private String service;
    private BigDecimal amount;
    private String unitOfMeasurement;
    private BigDecimal pricePerUnit;
    private BigDecimal cost;
    @XmlElement
    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
    @XmlElement
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    @XmlElement
    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }
    @XmlElement
    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
    @XmlElement
    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
