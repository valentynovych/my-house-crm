package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Invoice;
import com.example.myhouse24admin.entity.InvoiceItem;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public interface InvoiceItemSpecification {
    static Specification<InvoiceItem> byInvoiceId(Long invoiceId){
        return (root, query, builder) -> {
            Join<InvoiceItem, Invoice> invoiceJoin = root.join("invoice");
            return builder.equal(invoiceJoin.get("id"),invoiceId);
        };
    }
}
