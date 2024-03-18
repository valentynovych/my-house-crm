package com.example.myhouse24user.model.invoice;

import java.math.BigDecimal;
import java.util.List;

public class ViewInvoiceResponse {
    private String number;
    private List<InvoiceItemResponse> invoiceItemResponses;
    private BigDecimal totalPrice;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<InvoiceItemResponse> getInvoiceItemResponses() {
        return invoiceItemResponses;
    }

    public void setInvoiceItemResponses(List<InvoiceItemResponse> invoiceItemResponses) {
        this.invoiceItemResponses = invoiceItemResponses;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
