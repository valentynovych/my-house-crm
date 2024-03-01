package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.*;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public interface InvoiceSpecification {
    static Specification<Invoice> byDeleted(){
        return (root, query, builder) ->
                builder.equal(root.get("deleted"), false);
    }
    static Specification<Invoice> byNumberLike(String number){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("number")), "%"+number.toUpperCase()+"%");
    }
    static Specification<Invoice> byStatus(InvoiceStatus status){
        return (root, query, builder) ->
                builder.equal(root.get("status"), status);
    }
    static Specification<Invoice> byApartmentNumberLike(String apartmentNumber){
        return (root, query, builder) -> {
            Join<Invoice, Apartment> apartmentJoin = root.join("apartment");
            return builder.like(builder.upper(apartmentJoin.get("apartmentNumber")), "%" + apartmentNumber.toUpperCase() + "%");
        };
    }
    static Specification<Invoice> byOwnerId(Long ownerId){
        return (root, query, builder) -> {
            Join<Invoice, Apartment> apartmentJoin = root.join("apartment");
            Join<Apartment, ApartmentOwner> ownerJoin = apartmentJoin.join("owner");
            return builder.equal(ownerJoin.get("id"),  ownerId);
        };
    }
    static Specification<Invoice> byProcessed(boolean isProcessed){
        return (root, query, builder) ->
                builder.equal(root.get("isProcessed"), isProcessed);
    }
    static Specification<Invoice> byCreationDateGreaterThan(Instant dateFrom){
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("creationDate"), dateFrom);
    }
    static Specification<Invoice> byCreationDateLessThan(Instant dateTo){
        return (root, query, builder) ->
                builder.lessThan(root.get("creationDate"), dateTo);
    }
    static Specification<Invoice> byMonth(Integer month){
        return (root, query, builder) ->
                builder.equal(builder.function("month", Integer.class,root.get("creationDate")), month);
    }
}
