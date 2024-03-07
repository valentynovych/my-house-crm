package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CashSheetSpecification implements Specification<CashSheet> {

    private final Map<String, String> searchParams;
    private final String BY_NUMBER = "sheetNumber";
    private final String BY_DATE = "date";
    private final String BY_STATUS = "status";
    private final String BY_PAYMENT_ITEM = "paymentItem";
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
                    LocalDate date = LocalDate.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    Instant instant = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                    predicates.add(criteriaBuilder.equal(root.get("creationDate"), instant));
                }
                case BY_STATUS ->
                        predicates.add(criteriaBuilder.equal(root.get("isProcessed"), Boolean.valueOf(value)));
                case BY_PAYMENT_ITEM -> {
                    Join<CashSheet, PaymentItem> paymentItemJoin = root.join("paymentItem");
                    predicates.add(criteriaBuilder.equal(paymentItemJoin.get("id"), value));
                }
                case BY_OWNER -> {
                    Join<CashSheet, PersonalAccount> personalAccountJoin = root.join("personalAccount");
                    Join<PersonalAccount, Apartment> ownerJoin = personalAccountJoin.join("apartment");
                    ApartmentOwner owner = new ApartmentOwner();
                    owner.setId(Long.parseLong(value));
                    predicates.add(criteriaBuilder.equal(ownerJoin.get("owner"), owner));
                }
                case BY_PERSONAL_ACCOUNT -> {
                    Join<CashSheet, PersonalAccount> personalAccountJoin = root.join("personalAccount");
                    predicates.add(criteriaBuilder.equal(personalAccountJoin.get("id"), Long.parseLong(value)));
                }
                case BY_PAYMENT_TYPE ->
                        predicates.add(criteriaBuilder.equal(root.get("sheetType"), CashSheetType.valueOf(value)));

            }
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        });
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
