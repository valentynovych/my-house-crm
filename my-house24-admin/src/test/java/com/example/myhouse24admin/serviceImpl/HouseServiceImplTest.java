package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Floor;
import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.entity.Section;
import com.example.myhouse24admin.mapper.HouseMapper;
import com.example.myhouse24admin.model.houses.*;
import com.example.myhouse24admin.model.meterReadings.HouseNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.repository.HouseRepo;
import com.example.myhouse24admin.specification.HouseSpecification;
import com.example.myhouse24admin.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseServiceImplTest {

    @Mock
    private HouseRepo houseRepo;
    @Mock
    private HouseMapper houseMapper;
    @Mock
    private UploadFileUtil fileUtil;
    @InjectMocks
    private HouseServiceImpl houseService;
    private static House house;

    @BeforeEach
    void setUp() {
        house = new House();
        house.setId(1L);
        house.setName("test");
        house.setFloors(new ArrayList<>());
        house.setSections(new ArrayList<>());
        house.setDeleted(false);
        house.setAddress("Test address, st. Test");
        house.setImage1("test1.jpg");
        house.setImage2("test2.jpg");
        house.setImage3("test3.jpg");
        house.setImage4("test4.jpg");
        house.setImage5("test5.jpg");

        for (int i = 0; i < 5; i++) {
            house.getFloors().add(new Floor());
            house.getSections().add(new Section());
        }
    }

    @Test
    void addNewHouse() {
        // given
        List<MultipartFile> images = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MultipartFile image = new MockMultipartFile("TestFileName_" + i, new byte[]{1, 2, 3});
            images.add(image);
        }

        HouseAddRequest houseAddRequest = new HouseAddRequest();
        houseAddRequest.setName("test");
        houseAddRequest.setFloors(new ArrayList<>());
        houseAddRequest.setSections(new ArrayList<>());
        houseAddRequest.setAddress("Test address, st. Test");
        houseAddRequest.setStaffIds(new ArrayList<>());
        houseAddRequest.setImages(images);
        ArgumentCaptor<House> houseCaptor = ArgumentCaptor.forClass(House.class);

        // when
        when(houseMapper.houseAddRequestToHouse(houseAddRequest))
                .thenReturn(house);
        when(fileUtil.saveMultipartFile(any(MultipartFile.class)))
                .thenAnswer(new Answer<String>() {
                    private int invocationCount = 0;

                    @Override
                    public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                        return "TestFileName_" + invocationCount++;
                    }
                });
        when(houseRepo.save(houseCaptor.capture()))
                .thenReturn(house);

        // call the method
        houseService.addNewHouse(houseAddRequest);

        // then
        verify(houseMapper, times(1)).houseAddRequestToHouse(houseAddRequest);
        verify(fileUtil, times(5)).saveMultipartFile(any(MultipartFile.class));
        verify(houseRepo, times(1)).save(houseCaptor.capture());

        House capturedHouse = houseCaptor.getValue();
        capturedHouse.getFloors().forEach(floor -> assertEquals(house, floor.getHouse()));
        capturedHouse.getSections().forEach(section -> assertEquals(house, section.getHouse()));
        assertFalse(capturedHouse.isDeleted());
        assertEquals(house.getName(), capturedHouse.getName());
        assertEquals("TestFileName_0", capturedHouse.getImage1());
        assertEquals("TestFileName_1", capturedHouse.getImage2());
        assertEquals("TestFileName_2", capturedHouse.getImage3());
        assertEquals("TestFileName_3", capturedHouse.getImage4());
        assertEquals("TestFileName_4", capturedHouse.getImage5());
    }

    @Test
    void getHouses() {
        // given
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("page", "0");
        searchParams.put("pageSize", "10");
        searchParams.put("name", "testHouse");

        // when
        when(houseRepo.findAll(any(HouseSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(house, house, house), PageRequest.of(0, 10), 3));
        when(houseMapper.houseListToHouseShortResponseList(anyList()))
                .thenReturn(List.of(new HouseShortResponse(), new HouseShortResponse(), new HouseShortResponse()));

        // call the method
        Page<HouseShortResponse> houses = houseService.getHouses(0, 10, searchParams);

        // then
        verify(houseRepo, times(1)).findAll(any(HouseSpecification.class), any(Pageable.class));
        verify(houseMapper, times(1)).houseListToHouseShortResponseList(anyList());

        assertEquals(3, houses.getNumberOfElements());
        assertEquals(3, houses.getTotalElements());
        assertEquals(3, houses.getContent().size());
    }

    @Test
    void deleteHouseById() {
        // given
        Long houseId = 1L;
        ArgumentCaptor<House> houseCaptor = ArgumentCaptor.forClass(House.class);

        // when
        when(houseRepo.findById(houseId))
                .thenReturn(Optional.of(house));

        // call the method
        houseService.deleteHouseById(houseId);

        // then
        verify(houseRepo, times(1)).findById(houseId);
        verify(houseRepo, times(1)).save(houseCaptor.capture());

        House capturedHouse = houseCaptor.getValue();
        assertTrue(capturedHouse.isDeleted());
    }

    @Test
    void deleteHouseById_WhenHouseNotFound() {
        // given
        Long houseId = 1L;

        // when
        when(houseRepo.findById(houseId))
                .thenReturn(Optional.empty());

        // call the method
        assertThrows(EntityNotFoundException.class, () -> houseService.deleteHouseById(houseId));

        // then
        verify(houseRepo, times(1)).findById(houseId);
    }

    @Test
    void getHouseById() {
        // given
        Long houseId = 1L;
        HouseViewResponse houseViewResponse = new HouseViewResponse();
        houseViewResponse.setId(house.getId());
        houseViewResponse.setAddress(house.getAddress());
        houseViewResponse.setName(house.getName());
        houseViewResponse.setFloorsCount(house.getFloors().size());
        houseViewResponse.setSectionsCount(house.getSections().size());
        houseViewResponse.setImage1(house.getImage1());
        houseViewResponse.setImage2(house.getImage2());
        houseViewResponse.setImage3(house.getImage3());
        houseViewResponse.setImage4(house.getImage4());
        houseViewResponse.setImage5(house.getImage5());

        // when
        when(houseRepo.findById(houseId))
                .thenReturn(Optional.of(house));
        when(houseMapper.houseToHouseViewResponse(eq(house)))
                .thenReturn(houseViewResponse);

        // call the method
        HouseViewResponse response = houseService.getHouseById(houseId);

        // then
        verify(houseRepo, times(1)).findById(houseId);
        verify(houseMapper, times(1)).houseToHouseViewResponse(eq(house));

        assertEquals(houseViewResponse, response);
        assertEquals(houseId, response.getId());
    }

    @Test
    void getHouseResponseById() {
        // given
        Long houseId = 1L;
        HouseResponse houseResponse = new HouseResponse();
        houseResponse.setId(house.getId());
        houseResponse.setAddress(house.getAddress());
        houseResponse.setName(house.getName());
        houseResponse.setImage1(house.getImage1());

        house.getFloors().forEach(floor -> floor.setDeleted(false));
        house.getSections().forEach(section -> section.setDeleted(false));

        // when
        when(houseRepo.findById(houseId))
                .thenReturn(Optional.of(house));
        when(houseMapper.houseToHouseResponse(eq(house)))
                .thenReturn(houseResponse);

        // call the method
        HouseResponse response = houseService.getHouseResponseById(houseId);

        // then
        verify(houseRepo, times(1)).findById(houseId);
        verify(houseMapper, times(1)).houseToHouseResponse(eq(house));

        assertEquals(houseResponse, response);
        assertEquals(houseId, response.getId());
    }

    @Test
    void editHouse() {
        // given
        Long houseId = house.getId();

        List<MultipartFile> images = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MultipartFile image = new MockMultipartFile("TestFileName_" + i, new byte[]{1, 2, 3});
            images.add(image);
        }

        HouseEditRequest houseEditRequest = new HouseEditRequest();
        houseEditRequest.setName("test");
        houseEditRequest.setFloors(new ArrayList<>());
        houseEditRequest.setSections(new ArrayList<>());
        houseEditRequest.setAddress("Test address, st. Test");
        houseEditRequest.setImages(images);
        ArgumentCaptor<House> houseCaptor = ArgumentCaptor.forClass(House.class);

        // when
        when(houseRepo.findById(eq(houseId)))
                .thenReturn(Optional.of(house));
        doNothing().when(houseMapper).updateHouseFromHouseRequest(eq(house), eq(houseEditRequest));
        when(fileUtil.saveMultipartFile(any(MultipartFile.class)))
                .thenAnswer(new Answer<String>() {
                    private int invocationCount = 0;

                    @Override
                    public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                        return "TestFileName_" + invocationCount++;
                    }
                });
        when(houseRepo.save(houseCaptor.capture()))
                .thenReturn(house);

        // call the method
        houseService.editHouse(houseId, houseEditRequest);

        // then
        verify(houseRepo, times(1)).findById(houseId);
        verify(houseMapper, times(1)).updateHouseFromHouseRequest(eq(house), eq(houseEditRequest));
        verify(fileUtil, times(5)).saveMultipartFile(any(MultipartFile.class));
        verify(houseRepo, times(1)).save(houseCaptor.capture());

        House capturedHouse = houseCaptor.getValue();
        capturedHouse.getFloors().forEach(floor -> assertEquals(house, floor.getHouse()));
        capturedHouse.getSections().forEach(section -> assertEquals(house, section.getHouse()));
        assertFalse(capturedHouse.isDeleted());
        assertEquals(house.getName(), capturedHouse.getName());
        assertEquals("TestFileName_0", capturedHouse.getImage1());
        assertEquals("TestFileName_1", capturedHouse.getImage2());
        assertEquals("TestFileName_2", capturedHouse.getImage3());
        assertEquals("TestFileName_3", capturedHouse.getImage4());
        assertEquals("TestFileName_4", capturedHouse.getImage5());
    }

    @Test
    void getHousesForSelect() {
        // given
        SelectSearchRequest selectSearchRequest = new SelectSearchRequest("test", 1);
        List<House> houses = List.of(house, house);
        Page<House> housePage = new PageImpl<>(houses, PageRequest.of(0, 10), 2);
        List<HouseNameResponse> houseNameResponses = List.of(
                new HouseNameResponse(1L, "test"), new HouseNameResponse(2L, "test"));

        // when
        when(houseRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(housePage);
        when(houseMapper.houseListToHouseNameResponseList(eq(houses)))
                .thenReturn(houseNameResponses);

        // call the method
        Page<HouseNameResponse> houseNameResponsesFromService = houseService.getHousesForSelect(selectSearchRequest);

        // then
        verify(houseRepo, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(houseMapper, times(1)).houseListToHouseNameResponseList(eq(houses));

        assertEquals(houseNameResponses, houseNameResponsesFromService.getContent());

    }
}