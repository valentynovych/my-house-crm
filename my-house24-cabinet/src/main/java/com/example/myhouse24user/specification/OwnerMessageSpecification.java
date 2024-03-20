package com.example.myhouse24user.specification;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.Message;
import com.example.myhouse24user.entity.OwnerMessage;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OwnerMessageSpecification implements Specification<OwnerMessage> {

    private final Map<String, String> searchParams;
    private static final String BY_OWNER_EMAIL = "ownerEmail";
    private static final String BY_TEXT = "search";
    private static final String BY_ID = "id";
    private static final String BY_READ = "read";

    public OwnerMessageSpecification(Map<String, String> searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<OwnerMessage> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        searchParams.forEach((param, value) -> {
            if ( value != null && !value.isEmpty())
                switch (param) {
                    case BY_OWNER_EMAIL -> {
                        Join<OwnerMessage, ApartmentOwner> ownerJoin = root.join("apartmentOwner", JoinType.LEFT);
                        predicates.add(criteriaBuilder.equal(ownerJoin.get("email"), value));
                    }
                    case BY_TEXT -> {
                        Join<OwnerMessage, Message> messageJoin = root.join("message", JoinType.LEFT);
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(messageJoin.get("text"), "%" + value + "%"),
                                criteriaBuilder.like(messageJoin.get("subject"), "%" + value + "%"))
                        );
                    }
                    case BY_ID -> {
                        predicates.add(criteriaBuilder.equal(root.get("id"), Long.valueOf(value)));
                    }
                    case BY_READ -> {
                        predicates.add(criteriaBuilder.equal(root.get("isRead"), Boolean.valueOf(value)));
                    }
                    default -> throw new IllegalArgumentException("Invalid search parameter: " + param);
                }
        });

        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
