package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MasterRequestSpecification implements Specification<MasterRequest> {

    private final Map<String, String> searchParams;
    private static final String BY_NUMBER = "number";
    private static final String BY_DATE = "visitDate";
    private static final String BY_MASTER_TYPE = "masterType";
    private static final String BY_DESCRIPTION = "description";
    private static final String BY_APARTMENT = "apartment";
    private static final String BY_APARTMENT_OWNER = "apartmentOwner";
    private static final String BY_OWNER_PHONE = "phone";
    private static final String BY_MASTER = "master";
    private static final String BY_STATUS = "status";

    public MasterRequestSpecification(Map<String, String> searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<MasterRequest> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        searchParams.forEach((param, value) -> {
            switch (param) {
                case BY_NUMBER -> predicates.add(criteriaBuilder.equal(root.get("id"), Long.valueOf(value)));
                case BY_DATE -> {
                    LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    Instant dateFrom = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.greaterThanOrEqualTo(root.get("visitDate"), dateFrom),
                            criteriaBuilder.lessThanOrEqualTo(root.get("visitDate"), dateFrom.plus(1, ChronoUnit.DAYS)))
                    );
                }
                case BY_MASTER_TYPE -> {
                    Join<MasterRequest, Staff> staffJoin = root.join("staff", JoinType.LEFT);
                    Join<Staff, Role> staffRoleJoin = staffJoin.join("role", JoinType.LEFT);
                    predicates.add(criteriaBuilder.equal(staffRoleJoin.get("name"), value));
                }
                case BY_DESCRIPTION -> predicates.add(criteriaBuilder.like(root.get("description"), "%" + value + "%"));
                case BY_APARTMENT -> {
                    Join<MasterRequest, Apartment> apartmentJoin = root.join("apartment", JoinType.LEFT);
                    predicates.add(criteriaBuilder.like(apartmentJoin.get("apartmentNumber"), "%" + value + "%"));
                }
                case BY_APARTMENT_OWNER -> {
                    Join<MasterRequest, Apartment> apartmentJoin = root.join("apartment", JoinType.LEFT);
                    Join<Apartment, ApartmentOwner> apartmentOwnerJoin = apartmentJoin.join("owner");
                    predicates.add(criteriaBuilder.equal(apartmentOwnerJoin.get("id"), Long.valueOf(value)));
                }
                case BY_OWNER_PHONE ->
                        predicates.add(criteriaBuilder.like(root.get("apartmentOwnerPhone"), "%" + value + "%"));
                case BY_MASTER -> {
                    Join<MasterRequest, Staff> staffJoin = root.join("staff", JoinType.LEFT);
                    predicates.add(criteriaBuilder.equal(staffJoin.get("id"), Long.valueOf(value)));
                }
                case BY_STATUS ->
                        predicates.add(criteriaBuilder.equal(root.get("status"), MasterRequestStatus.valueOf(value)));
                default ->
                        throw new IllegalArgumentException(String.format("Filter as like '%s' - is not supported", param));
            }
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        });

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
