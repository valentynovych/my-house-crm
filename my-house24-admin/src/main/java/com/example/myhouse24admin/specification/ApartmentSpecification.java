package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApartmentSpecification implements Specification<Apartment> {
    private final Map<String, String> searchParams;

    public ApartmentSpecification(Map<String, String> searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<Apartment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();
        searchParams.forEach((key, value) -> {
            switch (key) {
                case "apartment" -> predicates.add(criteriaBuilder.equal(root.get("id"), Long.valueOf(value)));
                case "apartmentNumber" ->
                        predicates.add(criteriaBuilder.like(root.get("apartmentNumber"), "%" + value + "%"));
                case "house" -> {
                    House house = new House();
                    house.setId(Long.valueOf(value));
                    predicates.add(criteriaBuilder.equal(root.get("house"), house));
                }
                case "section" -> {
                    if (!value.isEmpty()) {
                        Section section = new Section();
                        section.setId(Long.valueOf(value));
                        predicates.add(criteriaBuilder.equal(root.get("section"), section));
                    }
                }
                case "floor" -> {
                    Floor floor = new Floor();
                    floor.setId(Long.valueOf(value));
                    predicates.add(criteriaBuilder.equal(root.get("floor"), floor));
                }
                case "owner" -> {
                    ApartmentOwner owner = new ApartmentOwner();
                    owner.setId(Long.valueOf(value));
                    predicates.add(criteriaBuilder.equal(root.get("owner"), owner));
                }
                case "balance" -> {
                    if (value.equals("arrears")) {
                        predicates.add(criteriaBuilder.lessThan(root.get("balance"), BigDecimal.ZERO));
                    } else if (value.equals("overpayment")) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("balance"), BigDecimal.ZERO));
                    }
                }
            }
            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
        });
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
