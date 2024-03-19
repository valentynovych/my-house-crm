package com.example.myhouse24user.specification;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.ApartmentOwner;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public interface ApartmentSpecification {
    static Specification<Apartment> byOwnerEmail(String email){
        return (root, query, builder) -> {
            Join<Apartment, ApartmentOwner> ownerJoin = root.join("owner");
            return builder.equal(ownerJoin.get("email"), email);
        };
    }
}
