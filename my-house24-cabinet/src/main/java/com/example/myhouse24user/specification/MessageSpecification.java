package com.example.myhouse24user.specification;


import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.Message;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageSpecification implements Specification<Message> {
    private final Map<String, String> searchParams;
    private static final String BY_OWNER_EMAIL = "ownerEmail";
    private static final String BY_TEXT = "search";
    private static final String BY_ID = "id";

    public MessageSpecification(Map<String, String> searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        searchParams.forEach((param, value) -> {
            if (!value.isEmpty())
                switch (param) {
                    case BY_OWNER_EMAIL -> {
                        Join<Message, ApartmentOwner> ownerJoin = root.join("apartmentOwners", JoinType.LEFT);
                        predicates.add(criteriaBuilder.equal(ownerJoin.get("email"), value));
                    }
                    case BY_TEXT -> predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get("text"), "%" + value + "%"),
                            criteriaBuilder.like(root.get("subject"), "%" + value + "%"))
                    );
                    case BY_ID -> predicates.add(criteriaBuilder.equal(root.get("id"), Long.valueOf(value)));
                }
        });
        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
