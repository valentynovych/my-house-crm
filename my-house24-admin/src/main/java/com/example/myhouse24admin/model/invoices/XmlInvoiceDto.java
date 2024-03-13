package com.example.myhouse24admin.model.invoices;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
@XmlRootElement(name = "invoice")
public class XmlInvoiceDto {
    private String number;
    private String creationDate;
    private String owner;
    private String personalAccount;
    private String house;
    private String section;
    private String apartment;
    private String tariff;
    private BigDecimal paid;
    private XmlInvoiceItemsDto invoiceItems;
    private BigDecimal total;

    @XmlElement
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    @XmlElement
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    @XmlElement
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    @XmlElement
    public String getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(String personalAccount) {
        this.personalAccount = personalAccount;
    }
    @XmlElement
    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }
    @XmlElement
    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
    @XmlElement
    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }
    @XmlElement
    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }
    @XmlElement
    public BigDecimal getPaid() {
        return paid;
    }

    public void setPaid(BigDecimal paid) {
        this.paid = paid;
    }

    @XmlElement
    public XmlInvoiceItemsDto getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(XmlInvoiceItemsDto invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    @XmlElement
    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
