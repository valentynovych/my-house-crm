package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.mapper.ApartmentOwnerMapper;
import com.example.myhouse24admin.mapper.InvoiceItemMapper;
import com.example.myhouse24admin.mapper.InvoiceMapper;
import com.example.myhouse24admin.model.invoices.*;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.InvoiceItemRepo;
import com.example.myhouse24admin.repository.InvoiceRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import com.example.myhouse24admin.service.CashRegisterService;
import com.example.myhouse24admin.service.PaymentItemService;
import com.example.myhouse24admin.service.StaffService;
import com.example.myhouse24admin.util.PdfGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {
    @Mock
    private InvoiceRepo invoiceRepo;
    @Mock
    private InvoiceItemRepo invoiceItemRepo;
    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private ServicesRepo servicesRepo;
    @Mock
    private ApartmentOwnerMapper apartmentOwnerMapper;
    @Mock
    private InvoiceMapper invoiceMapper;
    @Mock
    private InvoiceItemMapper invoiceItemMapper;
    @Mock
    private CashRegisterService cashRegisterService;
    @Mock
    private StaffService staffService;
    @Mock
    private PaymentItemService paymentItemService;
    @Mock
    private PdfGenerator pdfGenerator;
    @InjectMocks
    private InvoiceServiceImpl invoiceService;
    private static OwnerResponse expectedOwnerResponse;
    private static InvoiceRequest invoiceRequest;
    private static Apartment apartment;
    private static Invoice invoice;
    @BeforeAll
    public static void setUp() {
        expectedOwnerResponse = new OwnerResponse(1L,"name",
                "phone", 1L, "tariff");

        invoiceRequest = new InvoiceRequest();
        invoiceRequest.setApartmentId(1L);
        invoiceRequest.setTotalPrice(BigDecimal.valueOf(44));
        invoiceRequest.setPaid(BigDecimal.valueOf(44));
        InvoiceItemRequest itemRequest = new InvoiceItemRequest();
        itemRequest.setServiceId(1L);
        invoiceRequest.setItemRequests(List.of(itemRequest));

        apartment = new Apartment();
        apartment.setBalance(BigDecimal.valueOf(50));

        invoice = new Invoice();
        invoice.setNumber("0000000001");
        ApartmentOwner owner = new ApartmentOwner();
        owner.setEmail("email");
        apartment.setOwner(owner);
        invoice.setApartment(apartment);
    }

    @Test
    void createNumber_Should_Create_First_Number() {
        when(invoiceRepo.findLast()).thenReturn(Optional.empty());
        String number = invoiceService.createNumber();
        assertThat(number).isEqualTo("0000000001");

        verify(invoiceRepo, times(1)).findLast();

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void createNumber_Should_Create_Second_Number() {
        Invoice invoice = new Invoice();
        invoice.setNumber("0000000001");

        when(invoiceRepo.findLast()).thenReturn(Optional.of(invoice));
        String number = invoiceService.createNumber();
        assertThat(number).isEqualTo("0000000002");

        verify(invoiceRepo, times(1)).findLast();

        verifyNoMoreInteractions(invoiceRepo);
    }

    @Test
    void getOwnerResponse_Should_Return_OwnerResponse() {
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.of(new Apartment()));
        when(apartmentOwnerMapper.apartmentToOwnerResponse(any(Apartment.class)))
                .thenReturn(expectedOwnerResponse);

        OwnerResponse ownerResponse = invoiceService.getOwnerResponse(1L);
        assertThat(ownerResponse).usingRecursiveComparison().isEqualTo(expectedOwnerResponse);

        verify(apartmentRepo, times(1)).findById(anyLong());
        verify(apartmentOwnerMapper, times(1))
                .apartmentToOwnerResponse(any(Apartment.class));

        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }

    @Test
    void getOwnerResponse_Should_Throw_EntityNotFoundException() {
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService
                .getOwnerResponse(1L));

        verify(apartmentRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(apartmentRepo);
    }
    @Test
    void createInvoice() {
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.of(apartment));
        when(invoiceRepo.findLast()).thenReturn(Optional.empty());
        when(invoiceMapper.invoiceRequestToInvoice(any(InvoiceRequest.class), any(Apartment.class), anyString()))
                .thenReturn(new Invoice());
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(new Invoice());
        when(cashRegisterService.getNextSheetNumber()).thenReturn("000001");
        when(staffService.getCurrentStaff()).thenReturn(new Staff());
        when(paymentItemService.getDefaultPaymentItemForInvoices()).thenReturn(new PaymentItem());
        when(invoiceMapper.invoiceToCashSheet(anyString(), any(Invoice.class), any(Staff.class), any(PaymentItem.class)))
                .thenReturn(new CashSheet());
        doNothing().when(cashRegisterService).saveCashSheet(any(CashSheet.class));
        when(servicesRepo.findById(anyLong())).thenReturn(Optional.of(new Service()));
        when(invoiceItemMapper.invoiceItemRequestToInvoiceItem(any(InvoiceItemRequest.class), any(Service.class), any(Invoice.class)))
                .thenReturn(new InvoiceItem());
        when(invoiceItemRepo.save(any(InvoiceItem.class))).thenReturn(new InvoiceItem());

        invoiceService.createInvoice(invoiceRequest);

        verify(apartmentRepo, times(1)).findById(anyLong());
        verify(invoiceRepo, times(1)).findLast();
        verify(invoiceMapper, times(1))
                .invoiceRequestToInvoice(any(InvoiceRequest.class), any(Apartment.class),
                        anyString());
        verify(invoiceRepo, times(1)).save(any(Invoice.class));
        verify(cashRegisterService, times(1)).getNextSheetNumber();
        verify(staffService, times(1)).getCurrentStaff();
        verify(paymentItemService, times(1)).getDefaultPaymentItemForInvoices();
        verify(invoiceMapper, times(1))
                .invoiceToCashSheet(anyString(), any(Invoice.class), any(Staff.class),
                        any(PaymentItem.class));
        verify(cashRegisterService, times(1)).saveCashSheet(any(CashSheet.class));
        verify(servicesRepo, times(1)).findById(anyLong());
        verify(invoiceItemMapper, times(1))
                .invoiceItemRequestToInvoiceItem(any(InvoiceItemRequest.class),
                        any(Service.class), any(Invoice.class));
        verify(invoiceItemRepo, times(1)).save(any(InvoiceItem.class));

        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(invoiceMapper);
        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(cashRegisterService);
        verifyNoMoreInteractions(staffService);
        verifyNoMoreInteractions(paymentItemService);
        verifyNoMoreInteractions(servicesRepo);
        verifyNoMoreInteractions(invoiceItemMapper);
        verifyNoMoreInteractions(invoiceItemRepo);
    }
    @Test
    void createInvoice_Apartment_FindById_Should_Throw_EntityNotFoundException() {
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService
                .createInvoice(invoiceRequest));

        verify(apartmentRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(apartmentRepo);
    }
    @Test
    void createInvoice_Service_FindById_Should_Throw_EntityNotFoundException() {
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.of(apartment));
        when(invoiceRepo.findLast()).thenReturn(Optional.empty());
        when(invoiceMapper.invoiceRequestToInvoice(any(InvoiceRequest.class), any(Apartment.class), anyString()))
                .thenReturn(new Invoice());
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(new Invoice());
        when(cashRegisterService.getNextSheetNumber()).thenReturn("000001");
        when(staffService.getCurrentStaff()).thenReturn(new Staff());
        when(paymentItemService.getDefaultPaymentItemForInvoices()).thenReturn(new PaymentItem());
        when(invoiceMapper.invoiceToCashSheet(anyString(), any(Invoice.class), any(Staff.class), any(PaymentItem.class)))
                .thenReturn(new CashSheet());
        doNothing().when(cashRegisterService).saveCashSheet(any(CashSheet.class));
        when(servicesRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService
                .createInvoice(invoiceRequest));

        verify(apartmentRepo, times(1)).findById(anyLong());
        verify(invoiceRepo, times(1)).findLast();
        verify(invoiceMapper, times(1))
                .invoiceRequestToInvoice(any(InvoiceRequest.class), any(Apartment.class),
                        anyString());
        verify(invoiceRepo, times(1)).save(any(Invoice.class));
        verify(cashRegisterService, times(1)).getNextSheetNumber();
        verify(staffService, times(1)).getCurrentStaff();
        verify(paymentItemService, times(1)).getDefaultPaymentItemForInvoices();
        verify(invoiceMapper, times(1))
                .invoiceToCashSheet(anyString(), any(Invoice.class), any(Staff.class),
                        any(PaymentItem.class));
        verify(cashRegisterService, times(1)).saveCashSheet(any(CashSheet.class));
        verify(servicesRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(invoiceMapper);
        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(cashRegisterService);
        verifyNoMoreInteractions(staffService);
        verifyNoMoreInteractions(paymentItemService);
        verifyNoMoreInteractions(servicesRepo);
    }

    @Test
    void getInvoiceResponsesForTable() {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("page", "0");
        requestMap.put("pageSize", "1");
        requestMap.put("number", "001");
        requestMap.put("status", InvoiceStatus.PAID.toString());
        requestMap.put("apartmentNumber", "001");
        requestMap.put("ownerId", "1");
        requestMap.put("processed", "true");
        requestMap.put("creationDate", "12.03.1900");
        requestMap.put("monthDate", "03.2024");
        Pageable pageable = PageRequest.of(0,1);
        Invoice invoice = new Invoice();
        invoice.setId(1L);

        TableInvoiceResponse tableInvoiceResponse = new TableInvoiceResponse(1L, "number", InvoiceStatus.PAID,
                "12.08.2000", "apartment", "name",
                true,BigDecimal.valueOf(44),BigDecimal.valueOf(44));

        when(invoiceRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(invoice), pageable, 5));
        when(invoiceItemRepo.getItemsSumByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(33));
        when(invoiceMapper.invoiceToTableInvoiceResponse(any(Invoice.class), any(BigDecimal.class)))
                .thenReturn(tableInvoiceResponse);

        Page<TableInvoiceResponse> invoiceResponses = invoiceService.getInvoiceResponsesForTable(requestMap);
        assertThat(invoiceResponses.getContent()).hasSize(1);
        assertThat(invoiceResponses.getContent().get(0)).usingRecursiveComparison()
                .isEqualTo(tableInvoiceResponse);

        verify(invoiceRepo, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(invoiceItemRepo, times(1)).getItemsSumByInvoiceId(anyLong());
        verify(invoiceMapper, times(1))
                .invoiceToTableInvoiceResponse(any(Invoice.class), any(BigDecimal.class));

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(invoiceMapper);
    }

    @Test
    void getInvoiceResponse_Should_Return_InvoiceResponse() {
        InvoiceResponse expectedInvoiceResponse = new InvoiceResponse();
        expectedInvoiceResponse.setNumber("number");
        expectedInvoiceResponse.setProcessed(true);
        expectedInvoiceResponse.setStatus(InvoiceStatus.PAID);

        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(invoiceItemRepo.getItemsSumByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(33));
        when(invoiceItemRepo.findAll(any(Specification.class)))
                .thenReturn(List.of(new InvoiceItem()));
        when(invoiceItemMapper.invoiceItemListToInvoiceItemResponseList(anyList()))
                .thenReturn(List.of());
        when(invoiceMapper.invoiceToInvoiceResponse(any(Invoice.class), any(BigDecimal.class), anyList()))
                .thenReturn(expectedInvoiceResponse);

        InvoiceResponse invoiceResponse = invoiceService.getInvoiceResponse(1L);
        assertThat(invoiceResponse).usingRecursiveComparison()
                .isEqualTo(expectedInvoiceResponse);

        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(invoiceItemRepo, times(1)).getItemsSumByInvoiceId(anyLong());
        verify(invoiceItemRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceItemMapper, times(1))
                .invoiceItemListToInvoiceItemResponseList(anyList());
        verify(invoiceMapper, times(1))
                .invoiceToInvoiceResponse(any(Invoice.class), any(BigDecimal.class), anyList());


        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(invoiceItemMapper);
        verifyNoMoreInteractions(invoiceMapper);
    }

    @Test
    void getInvoiceResponse_Should_Throw_EntityNotFoundException() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService
                .getInvoiceResponse(1L));

        verify(invoiceRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void updateInvoice_Should_Update_Invoice() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.of(apartment));
        doNothing().when(invoiceMapper).updateInvoice(any(Invoice.class), any(InvoiceRequest.class), any(Apartment.class));
        when(invoiceItemRepo.findAll(any(Specification.class)))
                .thenReturn(List.of(new InvoiceItem()));
        doNothing().when(invoiceItemRepo).deleteAll(anyIterable());
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(new Invoice());
        when(servicesRepo.findById(anyLong())).thenReturn(Optional.of(new Service()));
        when(invoiceItemMapper.invoiceItemRequestToInvoiceItem(any(InvoiceItemRequest.class), any(Service.class), any(Invoice.class)))
                .thenReturn(new InvoiceItem());
        when(invoiceItemRepo.save(any(InvoiceItem.class))).thenReturn(new InvoiceItem());
        doNothing().when(cashRegisterService).updateCashSheetFromInvoice(any(Invoice.class));

        invoiceService.updateInvoice(1L, invoiceRequest);

        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(apartmentRepo, times(1)).findById(anyLong());
        verify(invoiceMapper, times(1))
                .updateInvoice(any(Invoice.class), any(InvoiceRequest.class), any(Apartment.class));
        verify(invoiceItemRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceItemRepo, times(1)).deleteAll(anyIterable());
        verify(invoiceRepo, times(1)).save(any(Invoice.class));
        verify(servicesRepo, times(1)).findById(anyLong());
        verify(invoiceItemMapper, times(1))
                .invoiceItemRequestToInvoiceItem(any(InvoiceItemRequest.class),
                        any(Service.class), any(Invoice.class));
        verify(invoiceItemRepo, times(1)).save(any(InvoiceItem.class));
        verify(cashRegisterService, times(1)).updateCashSheetFromInvoice(any(Invoice.class));

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(invoiceMapper);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(servicesRepo);
        verifyNoMoreInteractions(invoiceItemMapper);
        verifyNoMoreInteractions(cashRegisterService);

    }
    @Test
    void updateInvoice_Invoice_FindById_Should_Throw_EntityNotFoundException() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService
                .updateInvoice(1L, invoiceRequest));

        verify(invoiceRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void updateInvoice_Apartment_FindById_Should_Throw_EntityNotFoundException() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService
                .updateInvoice(1L, invoiceRequest));

        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(apartmentRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(apartmentRepo);

    }

    @Test
    void updateInvoice_Service_FindById_Should_Throw_EntityNotFoundException() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.of(apartment));
        doNothing().when(invoiceMapper).updateInvoice(any(Invoice.class), any(InvoiceRequest.class), any(Apartment.class));
        when(invoiceItemRepo.findAll(any(Specification.class)))
                .thenReturn(List.of(new InvoiceItem()));
        doNothing().when(invoiceItemRepo).deleteAll(anyIterable());
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(new Invoice());
        when(servicesRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService
                .updateInvoice(1L, invoiceRequest));

        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(apartmentRepo, times(1)).findById(anyLong());
        verify(invoiceMapper, times(1))
                .updateInvoice(any(Invoice.class), any(InvoiceRequest.class), any(Apartment.class));
        verify(invoiceItemRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceItemRepo, times(1)).deleteAll(anyIterable());
        verify(invoiceRepo, times(1)).save(any(Invoice.class));
        verify(servicesRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(invoiceMapper);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(servicesRepo);

    }

    @Test
    void getInvoiceResponseForView() {
        ViewInvoiceResponse exxpectedViewInvoiceResponse = new ViewInvoiceResponse();
        exxpectedViewInvoiceResponse.setNumber("number");
        exxpectedViewInvoiceResponse.setPhoneNumber("phone");
        exxpectedViewInvoiceResponse.setProcessed(true);

        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(invoiceItemRepo.getItemsSumByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(33));
        when(invoiceItemRepo.findAll(any(Specification.class)))
                .thenReturn(List.of(new InvoiceItem()));
        when(invoiceItemMapper.invoiceItemListToInvoiceItemResponseList(anyList()))
                .thenReturn(List.of());
        when(invoiceMapper.invoiceToViewInvoiceResponse(any(Invoice.class), anyList(), any(BigDecimal.class)))
                .thenReturn(exxpectedViewInvoiceResponse);

        ViewInvoiceResponse viewInvoiceResponse = invoiceService.getInvoiceResponseForView(1L);
        assertThat(viewInvoiceResponse).usingRecursiveComparison()
                .isEqualTo(exxpectedViewInvoiceResponse);

        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(invoiceItemRepo, times(1)).getItemsSumByInvoiceId(anyLong());
        verify(invoiceItemRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceItemMapper, times(1))
                .invoiceItemListToInvoiceItemResponseList(anyList());
        verify(invoiceMapper, times(1))
                .invoiceToViewInvoiceResponse(any(Invoice.class), anyList(), any(BigDecimal.class));

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(invoiceItemMapper);
        verifyNoMoreInteractions(invoiceMapper);
    }

    @Test
    void deleteInvoice_Should_Delete_Invoice() {
        invoice.setPaid(BigDecimal.valueOf(0));

        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(invoice));
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(new Invoice());

        boolean deleted = invoiceService.deleteInvoice(1L);
        assertThat(deleted).isTrue();

        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(invoiceRepo, times(1)).save(any(Invoice.class));

        verifyNoMoreInteractions(invoiceRepo);

    }
    @Test
    void deleteInvoice_Should_Not_Delete_Invoice() {
        invoice.setPaid(BigDecimal.valueOf(20));

        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(invoice));

        boolean deleted = invoiceService.deleteInvoice(1L);
        assertThat(deleted).isFalse();

        verify(invoiceRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void deleteInvoice_Should_Throw_EntityNotFoundException() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService
                .deleteInvoice(1L));

        verify(invoiceRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void deleteInvoices_Should_Delete_Invoices() {
        invoice.setPaid(BigDecimal.valueOf(0));
        when(invoiceRepo.findAllById(anyIterable())).thenReturn(List.of(invoice));
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(new Invoice());

        boolean deleted = invoiceService.deleteInvoices(new Long[]{1L});
        assertThat(deleted).isTrue();

        verify(invoiceRepo, times(1)).findAllById(anyIterable());
        verify(invoiceRepo, times(1)).save(any(Invoice.class));

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void deleteInvoices_Should_Not_Delete_Invoices() {
        invoice.setPaid(BigDecimal.valueOf(332));
        when(invoiceRepo.findAllById(anyIterable())).thenReturn(List.of(invoice));

        boolean deleted = invoiceService.deleteInvoices(new Long[]{1L});
        assertThat(deleted).isFalse();

        verify(invoiceRepo, times(1)).findAllById(anyIterable());

        verifyNoMoreInteractions(invoiceRepo);
    }


    @Test
    void getInvoiceNumber_Should_Return_Number() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(invoice));

        String number = invoiceService.getInvoiceNumber(1L);
        assertThat(number).isEqualTo("0000000001");

        verify(invoiceRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void getInvoiceNumber_Should_Throw_EntityNotFoundException() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService
                .getInvoiceNumber(1L));

        verify(invoiceRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void getInvoiceOwnerEmail_Should_Return_Owner_Email() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(invoice));

        String email = invoiceService.getInvoiceOwnerEmail(1L);
        assertThat(email).isEqualTo("email");

        verify(invoiceRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void getInvoiceOwnerEmail_Should_Throw_EntityNotFoundException() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService
                .getInvoiceOwnerEmail(1L));

        verify(invoiceRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void createPdfFile_Should_Create_File() throws IOException {
        mockForCreatePdfFile();
        when(pdfGenerator.formPdfFile(any(XmlInvoiceDto.class), anyString())).thenReturn(new byte[]{(byte)0xe0});

        byte[] file = invoiceService.createPdfFile(1L, "template");
        assertThat(file).hasSize(1);

        verifyForCreatePdfFile();
    }
    @Test
    void createPdfFile_Should_Throw_IOException() throws IOException {
        mockForCreatePdfFile();
        doThrow(IOException.class).when(pdfGenerator).formPdfFile(any(XmlInvoiceDto.class), anyString());

        byte[] file = invoiceService.createPdfFile(1L, "template");
        assertThat(file).hasSize(0);

        verifyForCreatePdfFile();
    }
    private void mockForCreatePdfFile(){
        when(invoiceItemRepo.findAll(any(Specification.class))).thenReturn(List.of(new InvoiceItem()));
        when(invoiceItemMapper.invoiceItemListToXmlListInvoiceItemDtoList(anyList()))
                .thenReturn(List.of(new XmlListInvoiceItemDto()));
        when(invoiceItemRepo.getItemsSumByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(32));
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(invoiceMapper.invoiceToXmlInvoiceDto(any(Invoice.class), any(XmlInvoiceItemsDto.class),
                any(BigDecimal.class))).thenReturn(new XmlInvoiceDto());
    }
    private void verifyForCreatePdfFile() throws IOException {
        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(invoiceItemRepo, times(1)).getItemsSumByInvoiceId(anyLong());
        verify(invoiceItemRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceItemMapper, times(1)).invoiceItemListToXmlListInvoiceItemDtoList(anyList());
        verify(invoiceMapper, times(1)).invoiceToXmlInvoiceDto(any(Invoice.class), any(XmlInvoiceItemsDto.class),
                any(BigDecimal.class));
        verify(pdfGenerator, times(1)).formPdfFile(any(XmlInvoiceDto.class), anyString());

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(invoiceMapper);
        verifyNoMoreInteractions(invoiceItemMapper);
        verifyNoMoreInteractions(pdfGenerator);
    }


}