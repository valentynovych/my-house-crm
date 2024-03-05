package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Message;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageSpecification implements Specification<Message> {
    private final Map<String, String> searchParams;
    private final static String BY_TEXT = "text";
    private final static String BY_DATE = "sendDate";

    public MessageSpecification(Map<String, String> searchParams) {

        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        searchParams.remove("page");
        searchParams.remove("pageSize");
        searchParams.forEach((param, value) -> {
            switch (param) {
                case BY_TEXT -> {
                    predicates.add(
                            criteriaBuilder.or(
                                    criteriaBuilder.like(root.get("text"), "%" + value + "%"),
                                    criteriaBuilder.like(root.get("subject"), "%" + value + "%"))
                    );
                }
                case BY_DATE -> {
                    LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    Instant date = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                    predicates.add(criteriaBuilder.equal(root.get("sendDate"), date));
                }
                default ->
                        throw new IllegalArgumentException(String.format("Filter as like '%s' - is not supported", param));
            }
        });

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
