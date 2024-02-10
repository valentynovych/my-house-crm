package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.House;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HouseSpecification implements Specification<House> {

    private final Map<String, String> searchParams;

    public HouseSpecification(Map<String, String> searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<House> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();
        searchParams.forEach((key, value) -> {
            switch (key) {
                case "name" -> predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                case "address" -> predicates.add(criteriaBuilder.like(root.get("address"), "%" + value + "%"));
            }
        });
        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
