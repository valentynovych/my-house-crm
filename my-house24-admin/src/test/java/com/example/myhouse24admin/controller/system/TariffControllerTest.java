package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.exception.TariffAlreadyUsedException;
import com.example.myhouse24admin.model.tariffs.TariffItemRequest;
import com.example.myhouse24admin.model.tariffs.TariffRequest;
import com.example.myhouse24admin.model.tariffs.TariffRequestWrap;
import com.example.myhouse24admin.model.tariffs.TariffResponse;
import com.example.myhouse24admin.service.TariffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
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
class TariffControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private TariffService tariffService;

    private static TariffResponse tariffResponse;

    @BeforeEach
    void setUp() {
        clearInvocations(tariffService);
        tariffResponse = new TariffResponse(
                1L,
                "test",
                "description",
                Instant.now(),
                new ArrayList<>());
    }

    @Test
    void viewTariff() throws Exception {

        // given
        var request = get("/admin/system-settings/tariffs")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("system/tariffs/tariffs")
                );
    }

    @Test
    void viewAddTariff() throws Exception {
        // given

        var request = get("/admin/system-settings/tariffs/add")
                .with(user(userDetails))
                .flashAttr("copyTariff", tariffResponse);

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("system/tariffs/add-tariff"),
                        model().attributeExists("copyTariff")
                );
    }

    @Test
    void viewEditTariff() throws Exception {
        // given
        var request = get("/admin/system-settings/tariffs/edit-tariff/1")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("system/tariffs/edit-tariff"));
    }

    @Test
    void viewTariffBuId() throws Exception {
        // given
        var request = get("/admin/system-settings/tariffs/view-tariff/1")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("system/tariffs/view-tariff"));
    }

    @Test
    void addNewTariff() throws Exception {
        // given
        var tariffRequest = new TariffRequest();
        tariffRequest.setName("test");
        tariffRequest.setDescription("description");
        TariffItemRequest tariffItemRequest = new TariffItemRequest();
        tariffItemRequest.setServicePrice(BigDecimal.valueOf(100));
        tariffItemRequest.setServiceId(1L);
        tariffRequest.setTariffItems(List.of(tariffItemRequest));
        var tariffRequestWrap = new TariffRequestWrap();
        tariffRequestWrap.setTariffRequest(tariffRequest);

        var request = post("/admin/system-settings/tariffs/add-tariff")
                .with(user(userDetails))
                .flashAttr("tariffRequest", tariffRequestWrap);

        // when
        doNothing().when(tariffService).addNewTariff(eq(tariffRequestWrap));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk()
                );
        verify(tariffService, times(1)).addNewTariff(any(TariffRequestWrap.class));
    }

    @Test
    void getAllTariffs() throws Exception {
        // given
        var pageable = Pageable.ofSize(10);
        var request = get("/admin/system-settings/tariffs/get-tariffs")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var tariffResponsePage = new PageImpl<>(
                List.of(tariffResponse), pageable, 1L);

        // when
        when(tariffService.getAllTariffs(eq(pageable.getPageNumber()), eq(pageable.getPageSize())))
                .thenReturn(tariffResponsePage);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "content": [{"id": 1, "name": "test", "description": "description"}], "numberOfElements": 1
                                }
                                """)
                );
        verify(tariffService, times(1)).getAllTariffs(eq(pageable.getPageNumber()), eq(pageable.getPageSize()));
    }

    @Test
    void testGetAllTariffs() throws Exception {
        // given
        var request = get("/admin/system-settings/tariffs/get-tariff-by-id/1")
                .with(user(userDetails));

        // when
        when(tariffService.getTariffById(eq(1L)))
                .thenReturn(tariffResponse);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "id": 1, "name": "test", "description": "description"
                                }
                                """)
                );
        verify(tariffService, times(1)).getTariffById(eq(1L));

    }

    @Test
    void editTariff() throws Exception {
        // given
        var tariffRequest = new TariffRequest();
        tariffRequest.setName("test");
        tariffRequest.setDescription("description");
        TariffItemRequest tariffItemRequest = new TariffItemRequest();
        tariffItemRequest.setServicePrice(BigDecimal.valueOf(100));
        tariffItemRequest.setServiceId(1L);
        tariffRequest.setTariffItems(List.of(tariffItemRequest));
        var tariffRequestWrap = new TariffRequestWrap();
        tariffRequestWrap.setTariffRequest(tariffRequest);

        var request = post("/admin/system-settings/tariffs/edit-tariff/1")
                .with(user(userDetails))
                .flashAttr("tariffRequest", tariffRequestWrap);

        // when
        doNothing().when(tariffService).editTariff(eq(1L), eq(tariffRequestWrap));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk()
                );
        verify(tariffService, times(1)).editTariff(eq(1L), any(TariffRequestWrap.class));
    }

    @Test
    void deleteTariff_WhenSuccessDelete() throws Exception {
        clearInvocations(tariffService);
        // given
        var request = delete("/admin/system-settings/tariffs/delete/1")
                .with(user(userDetails));

        // when
        when(tariffService.deleteTariffById(eq(1L)))
                .thenReturn(true);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk()
                );

        verify(tariffService, times(1)).deleteTariffById(eq(1L));
    }

    @Test
    void deleteTariff_WhenFailDelete() throws Exception {
        // given
        var request = delete("/admin/system-settings/tariffs/delete/1")
                .with(user(userDetails));

        // when
        when(tariffService.deleteTariffById(eq(1L)))
                .thenReturn(false);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );

        verify(tariffService, times(1)).deleteTariffById(eq(1L));
    }

    @Test
    void deleteTariff_WhenExceptionOnDelete() throws Exception {
        // given
        var request = delete("/admin/system-settings/tariffs/delete/1")
                .with(user(userDetails));

        // when
        when(tariffService.deleteTariffById(eq(1L)))
                .thenThrow(new TariffAlreadyUsedException("Tariff already used", "test"));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isConflict(),
                        header().stringValues("Content-Type", "text/html; charset=utf-8")
                );

        verify(tariffService, times(1)).deleteTariffById(eq(1L));
    }

    @Test
    void copyTariffById() throws Exception {
        // given
        var request = get("/admin/system-settings/tariffs/copy-tariff/1")
                .with(user(userDetails))
                .flashAttr("attributes", new RedirectAttributesModelMap());

        // when
        when(tariffService.getTariffById(eq(1L)))
                .thenReturn(tariffResponse);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/admin/system-settings/tariffs/add"),
                        flash().attribute("copyTariff", tariffResponse)
                );

        verify(tariffService, times(1)).getTariffById(eq(1L));
    }
}