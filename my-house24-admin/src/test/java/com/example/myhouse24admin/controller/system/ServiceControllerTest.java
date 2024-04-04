package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.exception.ServiceAlreadyUsedException;
import com.example.myhouse24admin.model.services.*;
import com.example.myhouse24admin.service.ServicesService;
import com.example.myhouse24admin.service.UnitOfMeasurementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private UnitOfMeasurementService unitOfMeasurementService;
    @Autowired
    private ServicesService servicesService;
    private static UnitOfMeasurementDto unitOfMeasurementDto;

    @BeforeEach
    void setUp() {
        clearInvocations(unitOfMeasurementService);
        clearInvocations(servicesService);
        unitOfMeasurementDto = new UnitOfMeasurementDto(1L, "testName");
    }

    @Test
    void viewServices() throws Exception {
        // given
        var request = get("/admin/system-settings/services")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("system/services/services"));
    }

    @Test
    void updateMeasurementUnits_WithoutDeletingUnits() throws Exception {
        // given
        var units = new UnitOfMeasurementDtoListWrap();
        units.setUnitOfMeasurements(List.of(unitOfMeasurementDto));
        var request = post("/admin/system-settings/services/update-measurement-unist")
                .with(user(userDetails))
                .flashAttr("units", units);

        // when
        doNothing().when(unitOfMeasurementService).updateMeasurementUnist(eq(units));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(unitOfMeasurementService, times(1)).updateMeasurementUnist(eq(units));
    }

    @Test
    void updateMeasurementUnits_WhenDeletingIsFailed() throws Exception {
        // given
        var units = new UnitOfMeasurementDtoListWrap();
        units.setUnitOfMeasurements(List.of(unitOfMeasurementDto));
        var request = post("/admin/system-settings/services/update-measurement-unist")
                .with(user(userDetails))
                .flashAttr("units", units);

        // when
        doThrow(new ServiceAlreadyUsedException("Test Exception", "Service Name"))
                .when(unitOfMeasurementService).updateMeasurementUnist(eq(units));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(header().stringValues("Content-Type", "text/html; charset=utf-8"));

        verify(unitOfMeasurementService, times(1)).updateMeasurementUnist(eq(units));
    }

    @Test
    void getAllMeasurementUnits() throws Exception {
        // given
        var request = get("/admin/system-settings/services/get-measurement-units")
                .with(user(userDetails));

        // when
        doReturn(List.of(unitOfMeasurementDto, unitOfMeasurementDto))
                .when(unitOfMeasurementService).getAllMeasurementUnits();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                                [
                                    {
                                        "id": 1, "name": "testName"
                                    },
                                    {
                                        "id": 1, "name": "testName"
                                    }
                                ]
                        """));

        verify(unitOfMeasurementService, times(1)).getAllMeasurementUnits();
    }

    @Test
    void getAllServices() throws Exception {
        // given
        var serviceResponse = new ServiceResponse(
                1L,
                "testName",
                true,
                unitOfMeasurementDto
        );
        var request = get("/admin/system-settings/services/get-services")
                .with(user(userDetails));

        // when
        doReturn(List.of(serviceResponse, serviceResponse))
                .when(servicesService).getAllServices();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                                [
                                    {
                                        "id": 1, "name": "testName", "showInMeter": true, "unitOfMeasurement": {
                                            "id": 1, "name": "testName"
                                        }
                                    },
                                    {
                                        "id": 1, "name": "testName", "showInMeter": true, "unitOfMeasurement": {
                                            "id": 1, "name": "testName"
                                        }
                                    }
                                ]
                        """));

        verify(servicesService, times(1)).getAllServices();
    }

    @Test
    void updateServices_WhenDeletingIsFailed() throws Exception {
        // given
        var serviceDto = new ServiceDto(1L, "testName", true, 1L);
        var services = new ServiceDtoListWrap();
        services.setServices(List.of(serviceDto, serviceDto));
        var request = post("/admin/system-settings/services/update-services")
                .with(user(userDetails))
                .flashAttr("servicesList", services);

        // when
        doThrow(new ServiceAlreadyUsedException("Test Exception", "Service Name"))
                .when(servicesService).updateServices(any(ServiceDtoListWrap.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(header().stringValues("Content-Type", "text/html; charset=utf-8"));

        verify(servicesService, times(1)).updateServices(any(ServiceDtoListWrap.class));
    }

    @Test
    void updateServices_WhenSuccessDelete() throws Exception {
        // given
        var serviceDto = new ServiceDto(1L, "testName", true, 1L);
        var services = new ServiceDtoListWrap();
        services.setServices(List.of(serviceDto, serviceDto));
        var request = post("/admin/system-settings/services/update-services")
                .with(user(userDetails))
                .flashAttr("servicesList", services);

        // when
        doNothing().when(servicesService).updateServices(any(ServiceDtoListWrap.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(servicesService, times(1)).updateServices(any(ServiceDtoListWrap.class));
    }

    @Test
    void getServiceById() throws Exception {
        // given
        var serviceResponse = new ServiceResponse(
                1L,
                "testName",
                true,
                unitOfMeasurementDto
        );
        var request = get("/admin/system-settings/services/get-service-by-id/1")
                .with(user(userDetails));

        // when
        doReturn(serviceResponse)
                .when(servicesService).getServiceById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                                    {
                                        "id": 1, "name": "testName", "showInMeter": true, "unitOfMeasurement": {
                                            "id": 1, "name": "testName"
                                        }
                                    }
                        """));

        verify(servicesService, times(1)).getServiceById(eq(1L));
    }
}