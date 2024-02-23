package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.CashSheet;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CashSheetSpecification implements Specification<CashSheet> {

    private final Map<String, String> searchParams;
    private final String BY_NUMBER = "sheetNumber";
    private final String BY_DATE = "date";
    private final String BY_STATUS = "status";
    private final String BY_PAYMENT_NAME = "paymentItemName";
    private final String BY_OWNER = "owner";
    private final String BY_PERSONAL_ACCOUNT = "personalAccount";
    private final String BY_PAYMENT_TYPE = "paymentType";

    public CashSheetSpecification(Map<String, String> searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<CashSheet> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        searchParams.forEach((param, value) -> {
            switch (param) {
                case BY_NUMBER -> predicates.add(criteriaBuilder.like(root.get("sheetNumber"), "%" + value + "%"));
                case BY_DATE -> {

                }
            }
        });
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
