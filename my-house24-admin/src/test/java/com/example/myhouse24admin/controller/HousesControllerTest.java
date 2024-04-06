package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.houses.*;
import com.example.myhouse24admin.service.HouseService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HousesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private HouseService houseService;

    @BeforeEach
    void setUp() {
        clearInvocations(houseService);
    }

    @Test
    void viewAddHouse() throws Exception {
        // given
        var request = get("/my-house/admin/houses/add")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("houses/add-house")
                );
    }

    @Test
    void testViewAddHouse() throws Exception {
        // given
        var request = get("/my-house/admin/houses/edit-house/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("houses/edit-house")
                );
    }

    @Test
    void viewHouses() throws Exception {
        // given
        var request = get("/my-house/admin/houses")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("houses/houses")
                );
    }

    @Test
    void viewHouseCard() throws Exception {
        // given
        var request = get("/my-house/admin/houses/view-house/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("houses/view-house")
                );
    }

    @Test
    void addNewHouse() throws Exception {
        // given
        var addRequest = new HouseAddRequest();
        addRequest.setName("test");
        addRequest.setAddress("test");
        addRequest.setStaffIds(List.of(1L, 2L));
        var sectionRequest = new SectionRequest();
        sectionRequest.setName("test");
        sectionRequest.setRangeApartmentNumbers("001-100");
        addRequest.setSections(List.of(sectionRequest));
        var floorRequest = new FloorRequest();
        floorRequest.setName("test");
        addRequest.setFloors(List.of(floorRequest));
        addRequest.setImages(List.of(new MockMultipartFile("image", "test".getBytes())));

        var request = post("/admin/houses/add")
                .with(user(userDetails))
                .flashAttr("request", addRequest);

        // when
        doNothing().when(houseService).addNewHouse(eq(addRequest));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(houseService, times(1)).addNewHouse(eq(addRequest));
    }

    @Test
    void getHouses() throws Exception {
        // given
        var pageable = Pageable.ofSize(10);
        var houseShortResponse = new HouseShortResponse();
        houseShortResponse.setId(1L);
        houseShortResponse.setName("test");
        houseShortResponse.setAddress("test");

        var request = get("/admin/houses/get-houses")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var houseShortResponses = new PageImpl<>(
                List.of(houseShortResponse, houseShortResponse), pageable, 2L);

        // when
        doReturn(houseShortResponses)
                .when(houseService).getHouses(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("test"))
                .andExpect(jsonPath("$.content[0].address").value("test"));

        verify(houseService, times(1))
                .getHouses(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());
    }

    @Test
    void deleteHouseById_WhenSuccessDelete() throws Exception {
        // given
        var request = delete("/admin/houses/delete/1")
                .with(user(userDetails));

        // when
        doReturn(true)
                .when(houseService).deleteHouseById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(houseService, times(1)).deleteHouseById(eq(1L));
    }

    @Test
    void deleteHouseById_WhenFailDelete() throws Exception {
        // given
        var request = delete("/admin/houses/delete/1")
                .with(user(userDetails));

        // when
        doThrow(new EntityNotFoundException("House not found"))
                .when(houseService).deleteHouseById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(houseService, times(1)).deleteHouseById(eq(1L));
    }

    @Test
    void getViewHouseById() throws Exception {
        // given
        var houseShortResponse = new HouseViewResponse();
        houseShortResponse.setId(1L);
        houseShortResponse.setName("test");
        houseShortResponse.setAddress("test");
        houseShortResponse.setImage1("test_image_1");
        houseShortResponse.setImage2("test_image_2");
        houseShortResponse.setImage3("test_image_3");
        houseShortResponse.setImage4("test_image_4");
        houseShortResponse.setImage5("test_image_5");
        houseShortResponse.setSectionsCount(2);
        houseShortResponse.setFloorsCount(2);

        var request = get("/admin/houses/get-view-house/1")
                .with(user(userDetails));

        // when
        doReturn(houseShortResponse)
                .when(houseService).getHouseById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.address").value("test"))
                .andExpect(jsonPath("$.image1").value("test_image_1"))
                .andExpect(jsonPath("$.image2").value("test_image_2"))
                .andExpect(jsonPath("$.image3").value("test_image_3"))
                .andExpect(jsonPath("$.image4").value("test_image_4"))
                .andExpect(jsonPath("$.image5").value("test_image_5"))
                .andExpect(jsonPath("$.sectionsCount").value(2))
                .andExpect(jsonPath("$.floorsCount").value(2));

        verify(houseService, times(1))
                .getHouseById(eq(1L));
    }

    @Test
    void getFullHouseById() throws Exception {
        // given
        var houseResponse = new HouseResponse();
        houseResponse.setId(1L);
        houseResponse.setName("test");
        houseResponse.setAddress("test");
        houseResponse.setImage1("test_image_1");
        houseResponse.setImage2("test_image_2");
        houseResponse.setImage3("test_image_3");
        houseResponse.setImage4("test_image_4");
        houseResponse.setImage5("test_image_5");

        var request = get("/admin/houses/get-house/1")
                .with(user(userDetails));

        // when
        doReturn(houseResponse)
                .when(houseService).getHouseResponseById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.address").value("test"))
                .andExpect(jsonPath("$.image1").value("test_image_1"))
                .andExpect(jsonPath("$.image2").value("test_image_2"))
                .andExpect(jsonPath("$.image3").value("test_image_3"))
                .andExpect(jsonPath("$.image4").value("test_image_4"))
                .andExpect(jsonPath("$.image5").value("test_image_5"))
                .andExpect(jsonPath("$.sections").isArray())
                .andExpect(jsonPath("$.floors").isArray());

        verify(houseService, times(1))
                .getHouseResponseById(eq(1L));
    }

    @Test
    void editHouse() throws Exception {
        // given
        var addRequest = new HouseEditRequest();
        addRequest.setName("test");
        addRequest.setAddress("test");
        var sectionRequest = new SectionRequest();
        sectionRequest.setName("test");
        sectionRequest.setRangeApartmentNumbers("001-100");
        addRequest.setSections(List.of(sectionRequest));
        var floorRequest = new FloorRequest();
        floorRequest.setName("test");
        addRequest.setFloors(List.of(floorRequest));
        addRequest.setImages(List.of(new MockMultipartFile("image", "test".getBytes())));
        var staffRequest = new StaffShortRequest();
        staffRequest.setId(1L);
        staffRequest.setFirstName("test");
        addRequest.setStaff(List.of(staffRequest));

        var request = post("/admin/houses/edit-house/1")
                .with(user(userDetails))
                .flashAttr("request", addRequest);

        // when
        doNothing().when(houseService).editHouse(eq(1L), eq(addRequest));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(houseService, times(1)).editHouse(eq(1L), eq(addRequest));
    }
}