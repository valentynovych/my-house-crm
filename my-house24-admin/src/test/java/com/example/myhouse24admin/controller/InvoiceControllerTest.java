package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.InvoiceStatus;
import com.example.myhouse24admin.entity.MeterReadingStatus;
import com.example.myhouse24admin.model.invoices.*;
import com.example.myhouse24admin.model.meterReadings.*;
import com.example.myhouse24admin.service.*;
import org.junit.jupiter.api.BeforeAll;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails userDetails;

    @Autowired
    private InvoiceService invoiceService;

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

    @Autowired
    private TariffService tariffService;

    @Autowired
    private  ApartmentOwnerService apartmentOwnerService;

    @Autowired
    private MailService mailService;

    private static Pageable pageable;
    private static InvoiceRequest invoiceRequest;
    @BeforeAll
    static void setUp() {
        pageable = PageRequest.of(0,1);

        invoiceRequest = new InvoiceRequest();
        invoiceRequest.setPaid(BigDecimal.valueOf(22));
        invoiceRequest.setTotalPrice(BigDecimal.valueOf(12));
        invoiceRequest.setHouse(1L);
        invoiceRequest.setApartmentId(1L);
        invoiceRequest.setCreationDate("12.03.1990");
        invoiceRequest.setStatus(InvoiceStatus.PAID);
        invoiceRequest.setProcessed(true);
        InvoiceItemRequest itemRequest = new InvoiceItemRequest();
        itemRequest.setCost(BigDecimal.valueOf(22));
        itemRequest.setPricePerUnit(BigDecimal.valueOf(22));
        itemRequest.setAmount(BigDecimal.valueOf(22));
        itemRequest.setServiceId(1L);
        invoiceRequest.setItemRequests(List.of(itemRequest));
    }

    @Test
    void getInvoicesPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/invoices")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/invoices"));
    }

    @Test
    void getInvoices() throws Exception {
        TableInvoiceResponse tableInvoiceResponse = new TableInvoiceResponse(1L, "number", InvoiceStatus.PAID,
                "12.08.2000", "apartment", "name",
                true, BigDecimal.valueOf(44),BigDecimal.valueOf(44));

        when(invoiceService.getInvoiceResponsesForTable(anyMap()))
                .thenReturn(new PageImpl<>(List.of(tableInvoiceResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/invoices/get")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("requestMap", String.valueOf(new HashMap<>())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].creationDate").value(tableInvoiceResponse.creationDate()))
                .andExpect(jsonPath("$.content[0].number").value(tableInvoiceResponse.number()));
    }

    @Test
    void getInvoicePage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/invoices/add")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/add-invoice"));
    }

    @Test
    void createInvoice_InvoiceRequest_Valid() throws Exception {
        doNothing().when(invoiceService).createInvoice(any(InvoiceRequest.class));

        this.mockMvc.perform(post("/my-house/admin/invoices/add")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("invoiceRequest", invoiceRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost/my-house/admin/invoices"));
    }
    @Test
    void createInvoice_InvoiceRequest_Not_Valid() throws Exception {
        doNothing().when(invoiceService).createInvoice(any(InvoiceRequest.class));

        this.mockMvc.perform(post("/my-house/admin/invoices/add")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("invoiceRequest", new InvoiceRequest()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(5)));
    }

    @Test
    void getStatuses() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/invoices/get-statuses")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(InvoiceStatus.PAID.toString()))
                .andExpect(jsonPath("$.[1]").value(InvoiceStatus.UNPAID.toString()))
                .andExpect(jsonPath("$.[2]").value(InvoiceStatus.PARTLY_PAID.toString()));
    }

    @Test
    void getHouses() throws Exception {
        HouseNameResponse houseNameResponse = new HouseNameResponse(1L, "house");
        when(houseService.getHousesForSelect(any(SelectSearchRequest.class)))
                .thenReturn(new PageImpl<>(List.of(houseNameResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/invoices/get-houses")
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

        this.mockMvc.perform(get("/my-house/admin/invoices/get-sections")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("search", "search")
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


        this.mockMvc.perform(get("/my-house/admin/invoices/get-apartments")
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
        when(servicesService.getServicesForSelect(any(SelectSearchRequest.class)))
                .thenReturn(new PageImpl<>(List.of(serviceNameResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/invoices/get-services")
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
    void getNumber() throws Exception {
        when(invoiceService.createNumber()).thenReturn("000001");

        this.mockMvc.perform(get("/my-house/admin/invoices/get-number")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("000001"));
    }

    @Test
    void getOwner() throws Exception {
        OwnerResponse ownerResponse = new OwnerResponse("1L", "name", "phone", 1L, "tariff");
        when(invoiceService.getOwnerResponse(anyLong())).thenReturn(ownerResponse);

        this.mockMvc.perform(get("/my-house/admin/invoices/get-owner")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("apartmentId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerFullName").value(ownerResponse.ownerFullName()))
                .andExpect(jsonPath("$.accountNumber").value(ownerResponse.accountNumber().toString()))
                .andExpect(jsonPath("$.ownerPhoneNumber").value(ownerResponse.ownerPhoneNumber()));
    }

    @Test
    void getMeterReadings() throws Exception {
        ApartmentMeterReadingResponse apartmentMeterReadingResponse =
                new ApartmentMeterReadingResponse(1L, "number", MeterReadingStatus.NEW,
                        "date", "house", "section",
                        "apartment", "service",
                        BigDecimal.valueOf(22), "measurement");

        when(meterReadingService.getMeterReadingResponsesForTableInInvoice(anyInt(), anyInt(), anyLong()))
                .thenReturn(new PageImpl<>(List.of(apartmentMeterReadingResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/invoices/get-meter-readings")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("page", "0")
                        .param("pageSize", "1")
                        .param("apartmentId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(apartmentMeterReadingResponse.id()))
                .andExpect(jsonPath("$.content[0].apartmentNumber").value(apartmentMeterReadingResponse.apartmentNumber()));
    }

    @Test
    void getTariffItems() throws Exception {
        TariffItemResponse tariffItemResponse = new TariffItemResponse(1L,
                "service", "unit", BigDecimal.valueOf(12));

        when(tariffService.getTariffItems(anyLong())).thenReturn(List.of(tariffItemResponse));

        this.mockMvc.perform(get("/my-house/admin/invoices/get-tariff-items")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("tariffId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].serviceName").value(tariffItemResponse.serviceName()))
                .andExpect(jsonPath("$.[0].serviceId").value(tariffItemResponse.serviceId()))
                .andExpect(jsonPath("$.[0].servicePrice").value(tariffItemResponse.servicePrice()));
    }

    @Test
    void getUnitOfMeasurement() throws Exception {
        UnitNameResponse unitNameResponse = new UnitNameResponse("name");
        when(servicesService.getUnitOfMeasurementNameByServiceId(anyLong())).thenReturn(unitNameResponse);

        this.mockMvc.perform(get("/my-house/admin/invoices/get-unit-name")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("serviceId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(unitNameResponse.name()));
    }

    @Test
    void getAmountOfConsumptions() throws Exception {
        when(meterReadingService.getAmountOfConsumptions(any(), anyLong()))
                .thenReturn(List.of(BigDecimal.valueOf(23), BigDecimal.valueOf(12)));

        this.mockMvc.perform(get("/my-house/admin/invoices/get-amounts")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("serviceIds[]", "1")
                        .param("apartmentId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(BigDecimal.valueOf(23)))
                .andExpect(jsonPath("$.[1]").value(BigDecimal.valueOf(12)));
    }

    @Test
    void getOwners() throws Exception {
        OwnerNameResponse ownerNameResponse = new OwnerNameResponse(1L, "name");

        when(apartmentOwnerService.getOwnerNameResponses(any(SelectSearchRequest.class)))
                .thenReturn(new PageImpl<>(List.of(ownerNameResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/invoices/get-owners")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("search", "search")
                        .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(ownerNameResponse.id()))
                .andExpect(jsonPath("$.content[0].name").value(ownerNameResponse.name()));
    }

    @Test
    void getEditInvoicePage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/invoices/edit/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/edit-invoice"));
    }

    @Test
    void getInvoice() throws Exception {
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        invoiceResponse.setStatus(InvoiceStatus.PAID);
        invoiceResponse.setNumber("001");

        when(invoiceService.getInvoiceResponse(anyLong())).thenReturn(invoiceResponse);

        this.mockMvc.perform(get("/my-house/admin/invoices/get-invoice/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(invoiceResponse.getStatus().toString()))
                .andExpect(jsonPath("$.number").value(invoiceResponse.getNumber()));
    }

    @Test
    void updateInvoice_InvoiceRequest_Valid() throws Exception {
        doNothing().when(invoiceService).updateInvoice(anyLong(), any(InvoiceRequest.class));

        this.mockMvc.perform(post("/my-house/admin/invoices/edit/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("invoiceRequest", invoiceRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost/my-house/admin/invoices"));
    }
    @Test
    void updateInvoice_InvoiceRequest_Not_Valid() throws Exception {
        doNothing().when(invoiceService).updateInvoice(anyLong(), any(InvoiceRequest.class));

        this.mockMvc.perform(post("/my-house/admin/invoices/edit/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("invoiceRequest", new InvoiceRequest()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(5)));
    }

    @Test
    void getViewInvoicePage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/invoices/view-invoice/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/view-invoice"));
    }

    @Test
    void getInvoiceForView() throws Exception {
        ViewInvoiceResponse viewInvoiceResponse = new ViewInvoiceResponse();
        viewInvoiceResponse.setNumber("0001");
        viewInvoiceResponse.setPhoneNumber("+380991234567");

        when(invoiceService.getInvoiceResponseForView(anyLong())).thenReturn(viewInvoiceResponse);

        this.mockMvc.perform(get("/my-house/admin/invoices/view-invoice/get/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(viewInvoiceResponse.getPhoneNumber()))
                .andExpect(jsonPath("$.number").value(viewInvoiceResponse.getNumber()));
    }

    @Test
    void deleteInvoice_Status_OK() throws Exception {
        when(invoiceService.deleteInvoice(anyLong())).thenReturn(true);

        this.mockMvc.perform(get("/my-house/admin/invoices/delete/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void deleteInvoice_Status_CONFLICT() throws Exception {
        when(invoiceService.deleteInvoice(anyLong())).thenReturn(false);

        this.mockMvc.perform(get("/my-house/admin/invoices/delete/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void deleteInvoices_Status_OK() throws Exception {
        when(invoiceService.deleteInvoices(any())).thenReturn(true);

        this.mockMvc.perform(get("/my-house/admin/invoices/delete-invoices")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("invoiceIds[]","1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void deleteInvoices_Status_CONFLICT() throws Exception {
        when(invoiceService.deleteInvoices(any())).thenReturn(false);

        this.mockMvc.perform(get("/my-house/admin/invoices/delete-invoices")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("invoiceIds[]","1"))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void getInvoicePageForCopy() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/invoices/copy/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/edit-invoice"));
    }

    @Test
    void saveCopiedInvoice_InvoiceRequest_Valid() throws Exception {
        doNothing().when(invoiceService).createInvoice(any(InvoiceRequest.class));

        this.mockMvc.perform(post("/my-house/admin/invoices/copy/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("invoiceRequest", invoiceRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost/my-house/admin/invoices"));
    }
    @Test
    void saveCopiedInvoice_InvoiceRequest_Not_Valid() throws Exception {
        doNothing().when(invoiceService).createInvoice(any(InvoiceRequest.class));
        this.mockMvc.perform(post("/my-house/admin/invoices/add")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("invoiceRequest", new InvoiceRequest()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(5)));
    }

    @Test
    void getPrintTemplatePage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/invoices/view-invoice/print/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/print-invoice"));

    }

    @Test
    void downloadInvoice() throws Exception {
        when(invoiceService.createPdfFile(anyLong(), anyString())).thenReturn(new byte[]{0x01});

        this.mockMvc.perform(get("/my-house/admin/invoices/view-invoice/print/download/{id}/{template}", 1, "template")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Disposition",
                        "attachment; filename="+"invoice_"+ LocalDate.now()+".pdf"));
    }

    @Test
    void getNumberById() throws Exception {
        when(invoiceService.getInvoiceNumber(anyLong())).thenReturn("000001");

        this.mockMvc.perform(get("/my-house/admin/invoices/get-number/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("000001"));
    }

    @Test
    void sendInvoice() throws Exception {
        when(invoiceService.getInvoiceOwnerEmail(anyLong())).thenReturn("email");
        when(invoiceService.createPdfFile(anyLong(), anyString())).thenReturn(new byte[]{0x01});
        doNothing().when(mailService).sendInvoice(anyString(), any());

        this.mockMvc.perform(post("/my-house/admin/invoices/view-invoice/send-invoice/{id}/{template}", 1, "template")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}