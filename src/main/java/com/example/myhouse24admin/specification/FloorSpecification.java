package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Floor;
import com.example.myhouse24admin.entity.House;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FloorSpecification implements Specification<Floor> {
    private final Map<String, String> searchParams;

    public FloorSpecification(Map<String, String> searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<Floor> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        searchParams.forEach((key, value) -> {
            switch (key) {
                case "name" -> predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                case "houseId" -> {
                    House house = new House();
                    house.setId(Long.valueOf(value));
                    predicates.add(criteriaBuilder.equal(root.get("house"), house));
                }
            }
        });
        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
