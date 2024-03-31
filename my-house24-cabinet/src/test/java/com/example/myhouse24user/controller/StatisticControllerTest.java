package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.statistic.GeneralOwnerStatistic;
import com.example.myhouse24user.model.statistic.StatisticDateItem;
import com.example.myhouse24user.model.statistic.StatisticItem;
import com.example.myhouse24user.service.StatisticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class StatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private StatisticService statisticService;

    @Test
    void viewStatistic() throws Exception {
        // given
        var request = get("/cabinet/statistic")
                .with(user(userDetails));
        // when
        mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/cabinet/statistic/1")
                );

    }

    @Test
    void viewStatisticByApartment() throws Exception {
        // given
        var request = get("/cabinet/statistic/1")
                .with(user(userDetails));
        // when
        mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("statistic/statistic")
                );
    }

    @Test
    void getGeneralStatisticByApartment() throws Exception {
        // given
        var request = get("/cabinet/statistic/get-general-statistic")
                .with(user(userDetails))
                .param("apartment", "1");

        // when
        doReturn(new GeneralOwnerStatistic(BigDecimal.ONE, "00000-00001", BigDecimal.valueOf(3)))
                .when(statisticService).getGeneralStatistic(eq(1L), any(Principal.class));
        mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                {
                                    "currentBalance": 1,
                                    "personalAccountNumber": "00000-00001",
                                    "expenseOnLastMonth": 3
                                }""")
                );
        verify(statisticService).getGeneralStatistic(eq(1L), any(Principal.class));
    }

    @Test
    void getExpensePerMonthByApartment() throws Exception {
        // given
        var request = get("/cabinet/statistic/get-expense-per-month")
                .with(user(userDetails))
                .param("apartment", "1");

        // when
        doReturn(List.of(new StatisticItem("firstItem", "firstValue")))
                .when(statisticService).getExpensePerMonthStatistic(eq(1L), any(Principal.class));
        mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                [
                                    {
                                        "itemName": "firstItem",
                                        "itemValue": "firstValue"
                                    }
                                ]
                                """));
        verify(statisticService).getExpensePerMonthStatistic(eq(1L), any(Principal.class));
    }

    @Test
    void getExpensePerYearByApartment() throws Exception {
        // given
        var request = get("/cabinet/statistic/get-expense-per-year")
                .with(user(userDetails))
                .param("apartment", "1");

        // when
        doReturn(List.of(new StatisticItem("firstItem", "firstValue")))
                .when(statisticService).getExpensePerYearStatistic(eq(1L), any(Principal.class));
        mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                [
                                    {
                                        "itemName": "firstItem",
                                        "itemValue": "firstValue"
                                    }
                                ]
                                """));
        verify(statisticService).getExpensePerYearStatistic(eq(1L), any(Principal.class));
    }

    @Test
    void getExpensePerYearOnMonthByApartment() throws Exception {
        // given
        var request = get("/cabinet/statistic/get-expense-per-year-on-month")
                .with(user(userDetails))
                .param("apartment", "1");

        // when
        doReturn(List.of(new StatisticDateItem(Instant.now(), BigDecimal.valueOf(100))))
                .when(statisticService).getExpensePerYearOnMonthStatistic(eq(1L), any(Principal.class));
        mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].month").isNotEmpty(),
                        jsonPath("$[0].amount").value((100)));
        verify(statisticService).getExpensePerYearOnMonthStatistic(eq(1L), any(Principal.class));
    }
}