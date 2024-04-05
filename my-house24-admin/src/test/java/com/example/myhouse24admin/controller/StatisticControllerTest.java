package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.statistic.BalanceStatistic;
import com.example.myhouse24admin.model.statistic.IncomeExpenseStatistic;
import com.example.myhouse24admin.model.statistic.InvoicePaidArrearsStatistic;
import com.example.myhouse24admin.model.statistic.StatisticGeneralResponse;
import com.example.myhouse24admin.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private StatisticService statisticService;

    @BeforeEach
    void setUp() {
        clearInvocations(statisticService);
    }

    @Test
    void getStatisticPage() throws Exception {
        // given
        var request = get("/my-house/admin/statistic").contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("statistic/statistic")
                );
    }

    @Test
    void getPersonalAccountsStatistic() throws Exception {
        // given
        var personalAccountsMetrics = new BalanceStatistic(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(3000)
        );
        var request = get("/admin/statistic/get-accounts-statistic")
                .with(user(userDetails));

        // when
        doReturn(personalAccountsMetrics)
                .when(statisticService).getPersonalAccountsMetrics();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "accountsBalanceArrears": 1000.0,
                            "accountsBalanceOverpayments": 2000.0,
                            "cashRegisterBalance": 3000.0
                        }
                        """)
                );

        verify(statisticService, times(1)).getPersonalAccountsMetrics();
    }

    @Test
    void getGeneralStatistic() throws Exception {
        // given
        var generalStatistic = new StatisticGeneralResponse(
                10,
                10,
                10,
                10,
                10,
                10
        );
        var request = get("/admin/statistic/get-general-statistic")
                .with(user(userDetails));

        // when
        doReturn(generalStatistic)
                .when(statisticService).getGeneralStatistic();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "countApartments": 10,
                            "countHouses": 10,
                            "countActiveApartmentOwners": 10,
                            "countPersonalAccounts": 10,
                            "countMasterRequestsInProgress": 10,
                            "countMasterRequestsNew": 10
                        }
                        """)
                );

        verify(statisticService, times(1)).getGeneralStatistic();
    }

    @Test
    void getGeneralStatistic_WhenThrowsInterruptedException() throws Exception {
        // given
        var request = get("/admin/statistic/get-general-statistic")
                .with(user(userDetails));

        // when
        doThrow(new InterruptedException())
                .when(statisticService).getGeneralStatistic();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(statisticService, times(1)).getGeneralStatistic();
    }

    @Test
    void getIncomeExpenseStatistic() throws Exception {
        // given
        IncomeExpenseStatistic statisticItem = new IncomeExpenseStatistic(
                Instant.now(),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(2000)
        );
        var incomeExpenseStatisticPerYear = List.of(statisticItem, statisticItem, statisticItem);
        var request = get("/admin/statistic/get-income-expense-statistic")
                .with(user(userDetails));

        // when
        doReturn(incomeExpenseStatisticPerYear)
                .when(statisticService).getIncomeExpenseStatisticPerYear();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].month").isNotEmpty())
                .andExpect(jsonPath("$[0].allIncomes").value(1000))
                .andExpect(jsonPath("$[0].allExpenses").value(2000));

        verify(statisticService, times(1)).getIncomeExpenseStatisticPerYear();
    }

    @Test
    void getInvoicesPaidArrearsStatistic() throws Exception {
        // given
        InvoicePaidArrearsStatistic statisticItem = new InvoicePaidArrearsStatistic(
                Instant.now(),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(2000)
        );
        var paidArrearsStatisticPerYear = List.of(statisticItem, statisticItem, statisticItem);
        var request = get("/admin/statistic/get-paid-arrears-statistic")
                .with(user(userDetails));

        // when
        doReturn(paidArrearsStatisticPerYear)
                .when(statisticService).getInvoicesPaidArrearsStatisticPerYear();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].month").isNotEmpty())
                .andExpect(jsonPath("$[0].arrears").value(1000))
                .andExpect(jsonPath("$[0].paidArrears").value(2000));

        verify(statisticService, times(1)).getInvoicesPaidArrearsStatisticPerYear();
    }
}