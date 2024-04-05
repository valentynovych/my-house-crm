package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.InvoiceStatus;
import com.example.myhouse24admin.entity.MeterReadingStatus;
import com.example.myhouse24admin.model.meterReadings.*;
import com.example.myhouse24admin.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class MeterReadingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails userDetails;

    @Autowired
    private HouseService houseService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private ServicesService servicesService;

    @Autowired
    private MeterReadingService meterReadingService;
    private Pageable pageable = PageRequest.of(0,1);;

    @Test
    void getMeterReadingsPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/meter-readings")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("meter-readings/meter-readings"));
    }

    @Test
    void getMeterReadings() throws Exception {
        TableMeterReadingResponse tableMeterReadingResponse =
                new TableMeterReadingResponse(1L, 1L, "house",
                        "section", "apartment", "service",
                        BigDecimal.valueOf(23), "measurement");

        when(meterReadingService.getMeterReadingResponsesForTable(anyMap()))
                .thenReturn(new PageImpl<>(List.of(tableMeterReadingResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/meter-readings/get")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("requestMap", String.valueOf(new HashMap<>())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].houseName").value(tableMeterReadingResponse.houseName()))
                .andExpect(jsonPath("$.content[0].sectionName").value(tableMeterReadingResponse.sectionName()));
    }

    @Test
    void getMeterReadingPageForCreate() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/meter-readings/add")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("meter-readings/meter-reading"))
                .andExpect(model().attributeExists("statusLink"))
                .andExpect(model().attributeExists("houseLink"))
                .andExpect(model().attributeExists("sectionLink"))
                .andExpect(model().attributeExists("apartmentLink"))
                .andExpect(model().attributeExists("serviceLink"));
    }

    @Test
    void getStatuses() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/meter-readings/get-statuses")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(MeterReadingStatus.NEW.toString()))
                .andExpect(jsonPath("$.[1]").value(MeterReadingStatus.INCLUDED.toString()))
                .andExpect(jsonPath("$.[2]").value(MeterReadingStatus.INCLUDED_AND_PAID.toString()))
                .andExpect(jsonPath("$.[3]").value(MeterReadingStatus.ZERO.toString()));
    }

    @Test
    void getHouses() throws Exception {
        HouseNameResponse houseNameResponse = new HouseNameResponse(1L, "house");
        when(houseService.getHousesForSelect(any(SelectSearchRequest.class)))
                .thenReturn(new PageImpl<>(List.of(houseNameResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/meter-readings/get-houses")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("search", "search")
                        .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(houseNameResponse.id()))
                .andExpect(jsonPath("$.content[0].name").value(houseNameResponse.name()));
    }

    @Test
    void getSections() throws Exception {
        SectionNameResponse sectionNameResponse = new SectionNameResponse(1L, "section");
        when(sectionService.getSectionForSelect(anyMap()))
                .thenReturn(new PageImpl<>(List.of(sectionNameResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/meter-readings/get-sections")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("requestMap", String.valueOf(new HashMap<>()))
                        .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(sectionNameResponse.id()))
                .andExpect(jsonPath("$.content[0].name").value(sectionNameResponse.name()));
    }

    @Test
    void getApartments() throws Exception {
        ApartmentNumberResponse apartmentNumberResponse = new ApartmentNumberResponse(1L, "23");
        when(apartmentService.getApartmentsForSelect(any(SelectSearchRequest.class), anyLong(), anyLong()))
                .thenReturn(new PageImpl<>(List.of(apartmentNumberResponse), pageable, 5));


        this.mockMvc.perform(get("/my-house/admin/meter-readings/get-apartments")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("search", "search")
                        .param("page", "1")
                        .param("houseId", "1")
                        .param("sectionId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(apartmentNumberResponse.id()))
                .andExpect(jsonPath("$.content[0].apartmentNumber").value(apartmentNumberResponse.apartmentNumber()));
    }

    @Test
    void getServices() throws Exception {
        ServiceNameResponse serviceNameResponse = new ServiceNameResponse(1L, "service");
        when(servicesService.getServicesForMeterReadingSelect(any(SelectSearchRequest.class)))
                .thenReturn(new PageImpl<>(List.of(serviceNameResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/meter-readings/get-services")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("search", "search")
                        .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(serviceNameResponse.id()))
                .andExpect(jsonPath("$.content[0].name").value(serviceNameResponse.name()));
    }

    @Test
    void createMeterReading_MeterReadingRequest_Valid() throws Exception {
        MeterReadingRequest meterReadingRequest =
                new MeterReadingRequest("12.03.1990", MeterReadingStatus.NEW,
                        BigDecimal.valueOf(22), 1L, 1L);

        doNothing().when(meterReadingService).createMeterReading(any(MeterReadingRequest.class));

        this.mockMvc.perform(post("/my-house/admin/meter-readings/add")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("notReturn", "true")
                        .flashAttr("meterReadingRequest", meterReadingRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost/my-house/admin/meter-readings/add"));
    }
    @Test
    void createMeterReading_MeterReadingRequest_Not_Valid() throws Exception {
        MeterReadingRequest meterReadingRequest =
                new MeterReadingRequest(null, null,
                        null, null, null);

        this.mockMvc.perform(post("/my-house/admin/meter-readings/add")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("notReturn", "true")
                        .flashAttr("meterReadingRequest", meterReadingRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(5)));
    }

    @Test
    void getMeterReadingPageForEdit() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/meter-readings/edit/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("meter-readings/meter-reading"))
                .andExpect(model().attributeExists("statusLink"))
                .andExpect(model().attributeExists("houseLink"))
                .andExpect(model().attributeExists("sectionLink"))
                .andExpect(model().attributeExists("apartmentLink"))
                .andExpect(model().attributeExists("serviceLink"));
    }

    @Test
    void getReading() throws Exception {
        MeterReadingResponse meterReadingResponse = new MeterReadingResponse("001",
                "12.03.1990", BigDecimal.valueOf(22), MeterReadingStatus.NEW,
                new HouseNameResponse(1L,"name"),
                new SectionNameResponse(1L, "name"),
                new ApartmentNumberResponse(1L, "11"),
                new ServiceNameResponse(1L, "name"));

        when(meterReadingService.getMeterReadingResponse(anyLong()))
                .thenReturn(meterReadingResponse);

        this.mockMvc.perform(get("/my-house/admin/meter-readings/get-reading/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(meterReadingResponse.number()))
                .andExpect(jsonPath("$.creationDate").value(meterReadingResponse.creationDate()))
                .andExpect(jsonPath("$.status").value(meterReadingResponse.status().toString()));
    }

    @Test
    void updateMeterReading_MeterReadingRequest_Valid() throws Exception {
        MeterReadingRequest meterReadingRequest =
                new MeterReadingRequest("12.03.1990", MeterReadingStatus.NEW,
                        BigDecimal.valueOf(22), 1L, 1L);

        doNothing().when(meterReadingService).updateMeterReading(anyLong(),any(MeterReadingRequest.class));

        this.mockMvc.perform(post("/my-house/admin/meter-readings/edit/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("notReturn", "true")
                        .flashAttr("meterReadingRequest", meterReadingRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost/my-house/admin/meter-readings/add"));
    }

    @Test
    void updateMeterReading_MeterReadingRequest_Not_Valid() throws Exception {
        MeterReadingRequest meterReadingRequest =
                new MeterReadingRequest(null, null,
                        null, null, null);

        this.mockMvc.perform(post("/my-house/admin/meter-readings/edit/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("notReturn", "true")
                        .flashAttr("meterReadingRequest", meterReadingRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(5)));
    }
    @Test
    void getNumber() throws Exception {
        when(meterReadingService.createNumber()).thenReturn("000001");

        this.mockMvc.perform(get("/my-house/admin/meter-readings/get-number")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("000001"));
    }

    @Test
    void getApartmentReadingsPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/meter-readings/apartment/{apartmentId}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("meter-readings/apartment-meter-readings"));
    }

    @Test
    void getMeterReadingsForApartment() throws Exception {
        ApartmentMeterReadingResponse apartmentMeterReadingResponse =
                new ApartmentMeterReadingResponse(1L, "001", MeterReadingStatus.NEW,
                        "12.03.1990", "house", "section",
                        "apartment", "service",
                        BigDecimal.valueOf(22), "measurement");

        when(meterReadingService.getApartmentMeterReadingResponses(anyLong(), anyMap()))
                .thenReturn(new PageImpl<>(List.of(apartmentMeterReadingResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/meter-readings/get-by-apartment/{apartmentId}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("requestMap", String.valueOf(new HashMap<>())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(apartmentMeterReadingResponse.id()))
                .andExpect(jsonPath("$.content[0].number").value(apartmentMeterReadingResponse.number()));
    }

    @Test
    void deleteReading() throws Exception {
        doNothing().when(meterReadingService).deleteMeterReading(anyLong());

        this.mockMvc.perform(get("/my-house/admin/meter-readings/delete/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}