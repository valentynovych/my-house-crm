package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.OwnerStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;

public interface ApartmentOwnerSpecification {
    static Specification<ApartmentOwner> byDeleted(){
        return (root, query, builder) ->
            builder.equal(root.get("deleted"), false);
    }
    static Specification<ApartmentOwner> byOwnerId(String ownerId){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("ownerId")), "%"+ownerId.toUpperCase()+"%");
    }
    static Specification<ApartmentOwner> byFirstName(String firstName){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("firstName")), "%"+firstName.toUpperCase()+"%");
    }
    static Specification<ApartmentOwner> byLastName(String lastName){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("lastName")), "%"+lastName.toUpperCase()+"%");
    }
    static Specification<ApartmentOwner> byMiddleName(String middleName){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("middleName")), "%"+middleName.toUpperCase()+"%");
    }
    static Specification<ApartmentOwner> byPhoneNumber(String phoneNumber){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("phoneNumber")), "%"+phoneNumber.toUpperCase()+"%");
    }
    static Specification<ApartmentOwner> byEmail(String email){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("email")), "%"+email.toUpperCase()+"%");
    }
    static Specification<ApartmentOwner> byCreationDateGreaterThan(Instant dateFrom){
        return (root, query, builder) ->
                builder.greaterThan(root.get("creationDate"), dateFrom);
    }
    static Specification<ApartmentOwner> byCreationDateLessThan(Instant dateTo){
        return (root, query, builder) ->
                builder.lessThan(root.get("creationDate"), dateTo);
    }
    static Specification<ApartmentOwner> byStatus(OwnerStatus status){
        return (root, query, builder) ->
                builder.equal(root.get("status"), status);
    }


}
