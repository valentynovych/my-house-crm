package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Floor;
import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.mapper.FloorMapper;
import com.example.myhouse24admin.model.houses.FloorResponse;
import com.example.myhouse24admin.repository.FloorRepo;
import com.example.myhouse24admin.specification.FloorSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FloorServiceImplTest {

    @Mock
    private FloorRepo floorRepo;
    @Mock
    private FloorMapper floorMapper;
    @InjectMocks
    private FloorServiceImpl floorService;

    @Test
    void getFloorsByHouseId() {
        // given
        String searchName = "Floor";
        Floor floor = new Floor();
        floor.setId(1L);
        floor.setHouse(new House());
        floor.setName("Floor 1");
        floor.setDeleted(false);

        FloorResponse floorResponse = new FloorResponse();
        floorResponse.setId(1L);
        floorResponse.setName("Floor 1");

        List<Floor> floors = List.of(floor, floor, floor);
        Page<Floor> floorPage = new PageImpl<>(floors, PageRequest.of(0, 10), floors.size());

        // when
        when(floorRepo.findAll(any(FloorSpecification.class), any(Pageable.class)))
                .thenReturn(floorPage);
        when(floorMapper.floorListToFloorResponseList(anyList()))
                .thenReturn(List.of(floorResponse, floorResponse, floorResponse));

        Page<FloorResponse> response = floorService.getFloorsByHouseId(1L, 0, 10, searchName);

        // then
        verify(floorRepo, times(1)).findAll(any(FloorSpecification.class), any(Pageable.class));
        verify(floorMapper, times(1)).floorListToFloorResponseList(anyList());

        assertEquals(3, response.getTotalElements());
        assertEquals(3, response.getContent().size());
    }

    @Test
    void deleteFloorsByHouseId(){
        when(floorRepo.findAll(any(FloorSpecification.class))).thenReturn(List.of(new Floor()));
        when(floorRepo.saveAll(anyList())).thenReturn(List.of(new Floor()));

        floorService.deleteFloorsByHouseId(1L);

        verify(floorRepo, times(1)).findAll(any(FloorSpecification.class));
        verify(floorRepo, times(1)).saveAll(anyList());

        verifyNoMoreInteractions(floorRepo);
    }
}