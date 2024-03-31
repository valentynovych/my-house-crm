package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.tariff.TariffItemResponse;
import com.example.myhouse24user.model.tariff.TariffResponse;
import com.example.myhouse24user.service.TariffService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class TariffControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private TariffService tariffService;

    @Test
    void viewTariff() throws Exception {
        // given
        var request = get("/cabinet/tariffs/1")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)
                // then
                .andExpectAll(
                        status().isOk(),
                        view().name("tariffs/tariff")
                );
    }

    @Test
    void getApartmentTariff() throws Exception {
        // given
        var request = get("/cabinet/tariffs/get-apartment-tariff/1")
                .with(user(userDetails));

        var tariffItem = new TariffItemResponse(
                1L,
                "serviceName",
                "unitOfMeasurement",
                BigDecimal.valueOf(12.0));
        var response = new TariffResponse(
                1L,
                "testTariff",
                List.of(tariffItem));
        // when
        doReturn(response).when(tariffService).getApartmentTariff(1L);

        this.mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                {
                                    "id": 1,
                                    "name": "testTariff",
                                    "tariffItems": [
                                        {
                                            "id": 1,
                                            "serviceName": "serviceName",
                                            "unitOfMeasurementName": "unitOfMeasurement",
                                            "servicePrice": 12
                                        }
                                        
                                    ]
                                }""")
                );
    }
}