package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.entity.StaffStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StaffSpecification implements Specification<Staff> {

    private final Map<String, String> searchParams;

    public StaffSpecification(Map<String, String> searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public Predicate toPredicate(Root<Staff> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        searchParams.forEach((key, value) -> {
            switch (key) {
                case "name" -> predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("firstName"), "%" + value + "%"),
                        criteriaBuilder.like(root.get("lastName"), "%" + value + "%")));
                case "phone" -> predicates.add(criteriaBuilder.like(root.get("phoneNumber"), "%" + value + "%"));
                case "email" -> predicates.add(criteriaBuilder.like(root.get("email"), "%" + value + "%"));
                case "status" -> predicates.add(criteriaBuilder.equal(root.get("status"), StaffStatus.valueOf(value)));
                case "role" -> {
                    Join<Role, Staff> roleStaffJoin = root.join("role");
                    predicates.add(criteriaBuilder.equal(roleStaffJoin.get("id"), Long.valueOf(value)));
                }
                case "roleName" -> {
                    if (!value.isEmpty()) {
                        Join<Role, Staff> roleStaffJoin = root.join("role");
                        predicates.add(criteriaBuilder.equal(roleStaffJoin.get("name"), value));
                    }
                }
            }
        });
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
