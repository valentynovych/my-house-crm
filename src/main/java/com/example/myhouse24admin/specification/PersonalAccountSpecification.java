package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.PersonalAccount;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

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
                        predicates.add(criteriaBuilder.like(root.get("accountNumber"), value));
                    }
                }
            }
        });

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
