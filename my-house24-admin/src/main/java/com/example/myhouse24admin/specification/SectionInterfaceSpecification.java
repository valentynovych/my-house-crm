package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.entity.Section;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public interface SectionInterfaceSpecification {
    static Specification<Section> byDeleted(){
        return (root, query, builder) ->
                builder.equal(root.get("deleted"), false);
    }
    static Specification<Section> byNameLike(String name){
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("name")), "%"+name.toUpperCase()+"%");
    }
    static Specification<Section> byHouseId(Long houseId){
        return (root, query, builder) ->{
            Join<Section, House> houseJoin = root.join("house");
            return builder.equal(houseJoin.get("id"), houseId);
        };
    }

    static Specification<Section> byApartmentNumberLike(String apartmentName){
        return (root, query, builder) ->{
            Join<Section, Apartment> apartmentJoin = root.join("apartments");
            return builder.like(builder.upper(apartmentJoin.get("apartmentNumber")), "%"+apartmentName.toUpperCase()+"%");
        };
    }
    static Specification<Section> byApartmentId(Long apartmentId){
        return (root, query, builder) ->{
            Join<Section, Apartment> apartmentJoin = root.join("apartments");
            return builder.equal(apartmentJoin.get("id"), apartmentId);
        };
    }

}
