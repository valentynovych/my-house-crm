package com.example.myhouse24user.specification;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.MasterRequest;
import com.example.myhouse24user.entity.MasterRequestStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public interface MasterRequestSpecification {

    static Specification<MasterRequest> byApartmentOwnerEmail(String ownerEmail) {
        return (root, query, criteriaBuilder) -> {
            Join<MasterRequest, Apartment> apartmentJoin = root.join("apartment", JoinType.LEFT);
            Join<Apartment, ApartmentOwner> ownerJoin = apartmentJoin.join("owner", JoinType.LEFT);
            return criteriaBuilder.equal(ownerJoin.get("email"), ownerEmail);
        };
    }

    static Specification<MasterRequest> byNotStatus(MasterRequestStatus masterRequestStatus) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("status"), masterRequestStatus);
    }
}
