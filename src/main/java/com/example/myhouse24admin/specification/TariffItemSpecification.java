package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Tariff;
import com.example.myhouse24admin.entity.TariffItem;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public interface TariffItemSpecification {
    static Specification<TariffItem> byTariffId(Long tariffId){
        return (root, query, builder) ->{
            Join<TariffItem, Tariff> tariffJoin = root.join("tariff");
            return builder.equal(tariffJoin.get("id"), tariffId);
        };
    }
}
