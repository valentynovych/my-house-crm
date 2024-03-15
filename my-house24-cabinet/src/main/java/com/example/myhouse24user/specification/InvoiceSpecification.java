package com.example.myhouse24user.specification;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.Invoice;
import com.example.myhouse24user.entity.InvoiceStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public interface InvoiceSpecification {
    static Specification<Invoice> byDeleted(){
        return (root, query, builder) ->
                builder.equal(root.get("deleted"), false);
    }
    static Specification<Invoice> byNumber(String number){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("number")), "%"+number.toUpperCase()+"%");
    }
    static Specification<Invoice> byStatus(InvoiceStatus status){
        return (root, query, builder) ->
                builder.equal(root.get("status"), status);
    }
    static Specification<Invoice> byCreationDate(LocalDate date){
        return (root, query, builder) ->
                builder.equal(builder.function("date", LocalDate.class,root.get("creationDate")), date);
    }
    static Specification<Invoice> byOwnerEmail(String email){
        return (root, query, builder) -> {
            Join<Invoice, Apartment> apartmentJoin = root.join("apartment");
            Join<Apartment, ApartmentOwner> ownerJoin = apartmentJoin.join("owner");
            return builder.equal(ownerJoin.get("email"), email);
        };
    }
}
