package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Service;
import org.springframework.data.jpa.domain.Specification;

public interface ServiceSpecification {
    static Specification<Service> byDeleted(){
        return (root, query, builder) ->
                builder.equal(root.get("deleted"), false);
    }
    static Specification<Service> byNameLike(String name){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("name")), "%"+name.toUpperCase()+"%");
    }
    static Specification<Service> byShowInMeter(){
        return (root, query, builder) ->
                builder.equal(root.get("showInMeter"), true);
    }
}
