package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.PaymentItem;
import com.example.myhouse24admin.entity.PaymentType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentItemSpecification implements Specification<PaymentItem> {

    private final Map<String, String> searchParams;
    private final String BY_NAME = "name";
    private final String BY_PAYMENT_TYPE = "paymentType";

    public PaymentItemSpecification(Map<String, String> searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<PaymentItem> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        searchParams.remove("page");
        searchParams.remove("pageSize");

        searchParams.forEach((param, value) -> {
            switch (param) {
                case BY_NAME -> predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                case BY_PAYMENT_TYPE ->
                        predicates.add(criteriaBuilder.equal(root.get("paymentType"), PaymentType.valueOf(value)));
                default -> throw new IllegalArgumentException("Filter by field: %s - not support");
            }
        });

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
