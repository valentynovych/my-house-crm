package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Tariff;
import org.springframework.data.jpa.domain.Specification;

public interface TariffSpecification {
    static Specification<Tariff> byDeleted(){
        return (root, query, builder) ->
                builder.equal(root.get("deleted"), false);
    }
    static Specification<Tariff> byTariffName(String name){
        return (root, query, builder) ->
                builder.equal(root.get("name"), name);
    }
}
