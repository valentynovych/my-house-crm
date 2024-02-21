package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public interface MeterReadingSpecification {
    static Specification<MeterReading> byDeleted(){
        return (root, query, builder) ->
                builder.equal(root.get("deleted"), false);
    }
    static Specification<MeterReading> byHouseId(Long houseId){
        return (root, query, builder) -> {
            Join<MeterReading, Apartment> apartmentJoin = root.join("apartment");
            Join<Apartment, House> houseJoin = apartmentJoin.join("house");
            return builder.equal(houseJoin.get("id"), houseId);
        };
    }
    static Specification<MeterReading> bySectionId(Long sectionId){
        return (root, query, builder) -> {
            Join<MeterReading, Apartment> apartmentJoin = root.join("apartment");
            Join<Apartment, Section> sectionJoin = apartmentJoin.join("section");
            return builder.equal(sectionJoin.get("id"), sectionId);
        };
    }
    static Specification<MeterReading> byServiceId(Long serviceId){
        return (root, query, builder) -> {
            Join<MeterReading, Service> serviceJoin = root.join("service");
            return builder.equal(serviceJoin.get("id"), serviceId);
        };
    }
    static Specification<MeterReading> byApartmentNumber(Integer apartmentNumber){
        return (root, query, builder) -> {
            Join<MeterReading, Apartment> apartmentJoin = root.join("apartment");
            return builder.equal(apartmentJoin.get("apartmentNumber"), apartmentNumber);
        };
    }
    static Specification<MeterReading> byMaxCreationDate(){
        return (root, query, builder) -> {
            Subquery<Number> sub = query.subquery(Number.class);
            Root<MeterReading> subRoot = sub.from(MeterReading.class);
            Join<MeterReading, Apartment> apartmentJoin = subRoot.join("apartment");
            sub.select(builder.max(subRoot.get("creationDate"))).groupBy(apartmentJoin.get("id"));
            return root.get("creationDate").in(sub);
        };
    }

}
