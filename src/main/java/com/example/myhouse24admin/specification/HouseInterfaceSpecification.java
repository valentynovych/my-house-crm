package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.entity.Section;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public interface HouseInterfaceSpecification {
    static Specification<House> byDeleted(){
        return (root, query, builder) ->
                builder.equal(root.get("deleted"), false);
    }
    static Specification<House> byNameLike(String name){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("name")), "%"+name.toUpperCase()+"%");
    }


}
