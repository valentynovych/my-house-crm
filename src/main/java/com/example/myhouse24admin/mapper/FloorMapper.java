package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Floor;
import com.example.myhouse24admin.model.houses.FloorRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FloorMapper {

    Floor floorRequestToFloor(FloorRequest floorRequest);

    FloorRequest floorToFloorRequest(Floor floor);
}
