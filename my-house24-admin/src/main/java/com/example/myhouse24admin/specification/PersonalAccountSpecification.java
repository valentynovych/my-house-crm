package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersonalAccountSpecification implements Specification<PersonalAccount> {

    private final Map<String, String> searchParams;

    public PersonalAccountSpecification(Map<String, String> searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<PersonalAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        searchParams.forEach((key, value) -> {
            switch (key) {
                case "accountNumber" -> {
                    if (!value.isEmpty()) {
                        predicates.add(criteriaBuilder.like(root.get("accountNumber"), "%" + value + "%"));
                    }
                }
                case "status" -> predicates.add(
                        criteriaBuilder.equal(root.get("status"), PersonalAccountStatus.valueOf(value)));
                case "apartmentNumber" -> {
                    Join<PersonalAccount, Apartment> apartmentJoin = root.join("apartment");
                    predicates.add(criteriaBuilder.like(apartmentJoin.get("apartmentNumber"), "%" + value + "%"));
                }
                case "house" -> {
                    Join<PersonalAccount, Apartment> apartmentJoin = root.join("apartment");
                    House house = new House();
                    house.setId(Long.valueOf(value));
                    predicates.add(criteriaBuilder.equal(apartmentJoin.get("house"), house));
                }
                case "section" -> {
                    Join<PersonalAccount, Apartment> apartmentJoin = root.join("apartment");
                    Section section = new Section();
                    section.setId(Long.valueOf(value));
                    predicates.add(criteriaBuilder.equal(apartmentJoin.get("section"), section));
                }
                case "owner" -> {
                    Join<PersonalAccount, Apartment> apartmentJoin = root.join("apartment");
                    ApartmentOwner owner = new ApartmentOwner();
                    owner.setId(Long.valueOf(value));
                    predicates.add(criteriaBuilder.equal(apartmentJoin.get("owner"), owner));
                }
                case "balance" -> {
                    Join<PersonalAccount, Apartment> apartmentJoin = root.join("apartment");
                    if (value.equals("arrears")) {
                        predicates.add(criteriaBuilder.lessThan(apartmentJoin.get("balance"), BigDecimal.ZERO));
                    } else if (value.equals("overpayment")) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(apartmentJoin.get("balance"), BigDecimal.ZERO));
                    }
                }
                case "apartmentNull" -> predicates.add(criteriaBuilder.isNull(root.get("apartment")));
            }
        });

        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
