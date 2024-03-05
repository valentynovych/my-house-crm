package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.entity.Section;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public interface ApartmentInterfaceSpecification {
    static Specification<Apartment> byDeleted(){
        return (root, query, builder) ->
                builder.equal(root.get("deleted"), false);
    }
    static Specification<Apartment> byNumberLike(String number){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("apartmentNumber")), "%"+number.toUpperCase()+"%");
    }
    static Specification<Apartment> bySectionId(Long sectionId){
        return (root, query, builder) -> {
            Join<Apartment, Section> sectionJoin = root.join("section");
            return builder.equal(sectionJoin.get("id"), sectionId);
        };
    }
    static Specification<Apartment> byHouseId(Long houseId){
        return (root, query, builder) -> {
            Join<Apartment, House> houseJoin = root.join("house");
            return builder.equal(houseJoin.get("id"), houseId);
        };
    }
    static Specification<Apartment> byOwnerId(Long id){
        return (root, query, builder) -> {
            Join<Apartment, ApartmentOwner> ownerJoin = root.join("owner");
            return builder.equal(ownerJoin.get("id"), id);
        };
    }
    static Specification<Apartment> byApartmentBalanceLessThanZero(){
        return (root, query, builder) ->
                builder.lessThan(root.get("balance"), 0);
    }
}
