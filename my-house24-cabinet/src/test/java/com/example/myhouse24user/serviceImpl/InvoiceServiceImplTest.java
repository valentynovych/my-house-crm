package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.*;
import com.example.myhouse24user.mapper.InvoiceItemMapper;
import com.example.myhouse24user.mapper.InvoiceMapper;
import com.example.myhouse24user.model.invoice.*;
import com.example.myhouse24user.model.owner.ApartmentOwnerDetails;
import com.example.myhouse24user.model.owner.ViewOwnerResponse;
import com.example.myhouse24user.repository.InvoiceItemRepo;
import com.example.myhouse24user.repository.InvoiceRepo;
import com.example.myhouse24user.repository.InvoiceTemplateRepo;
import com.example.myhouse24user.util.PdfGenerator;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {
    @Mock
    private InvoiceRepo invoiceRepo;
    @Mock
    private InvoiceItemRepo invoiceItemRepo;
    @Mock
    private InvoiceTemplateRepo invoiceTemplateRepo;
    @Mock
    private InvoiceMapper invoiceMapper;
    @Mock
    private InvoiceItemMapper invoiceItemMapper;
    @Mock
    private PdfGenerator pdfGenerator;
    @InjectMocks
    private InvoiceServiceImpl invoiceService;
    private static ViewInvoiceResponse expectedViewInvoiceResponse;
    private static InvoiceTemplate invoiceTemplate;
    private static InvoiceResponse expectedInvoiceResponse;
    @BeforeAll
    public static void setUp() {
        expectedViewInvoiceResponse = new ViewInvoiceResponse();
        expectedViewInvoiceResponse.setNumber("number");
        expectedViewInvoiceResponse.setTotalPrice(BigDecimal.valueOf(32));
        expectedViewInvoiceResponse.setInvoiceItemResponses(List.of(new InvoiceItemResponse()));

        invoiceTemplate = new InvoiceTemplate();
        invoiceTemplate.setFile("file");

        expectedInvoiceResponse = new InvoiceResponse(1L, "001", "12.09.1900",
                InvoiceStatus.PAID, BigDecimal.valueOf(22), BigDecimal.valueOf(45));

        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setEmail("email");
        apartmentOwner.setOwnerId("0002");
        ApartmentOwnerDetails apartmentOwnerDetails = new ApartmentOwnerDetails(apartmentOwner);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(apartmentOwnerDetails);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getViewInvoiceResponse_Should_Return_ViewOwnerResponse() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(invoiceItemRepo.getItemsSumByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(32));
        when(invoiceItemRepo.findAll(any(Specification.class))).thenReturn(List.of(new InvoiceItem()));
        when(invoiceItemMapper.invoiceItemListToInvoiceItemResponseList(anyList()))
                .thenReturn(List.of(new InvoiceItemResponse()));
        when(invoiceMapper.invoiceToViewInvoiceResponse(any(Invoice.class),anyList(),any(BigDecimal.class)))
                .thenReturn(expectedViewInvoiceResponse);

        ViewInvoiceResponse viewInvoiceResponse = invoiceService.getViewInvoiceResponse(1L);
        assertThat(viewInvoiceResponse).usingRecursiveComparison().isEqualTo(expectedViewInvoiceResponse);

        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(invoiceItemRepo, times(1)).getItemsSumByInvoiceId(anyLong());
        verify(invoiceItemRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceItemMapper, times(1)).invoiceItemListToInvoiceItemResponseList(anyList());
        verify(invoiceMapper, times(1)).invoiceToViewInvoiceResponse(any(Invoice.class),anyList(),any(BigDecimal.class));

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(invoiceMapper);
        verifyNoMoreInteractions(invoiceItemMapper);
    }
    @Test
    void getViewInvoiceResponse_Should_Throw_EntityNotFoundException() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService.getViewInvoiceResponse(1L));

        verify(invoiceRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
    }

    @Test
    void createPdfFile_Should_Use_Default_Template() throws IOException {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(invoiceItemRepo.findAll(any(Specification.class))).thenReturn(List.of(new InvoiceItem()));
        when(invoiceItemMapper.invoiceItemListToXmlListInvoiceItemDtoList(anyList()))
                .thenReturn(List.of(new XmlListInvoiceItemDto()));
        when(invoiceItemRepo.getItemsSumByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(32));
        when(invoiceMapper.invoiceToXmlInvoiceDto(any(Invoice.class), any(XmlInvoiceItemsDto.class),
                any(BigDecimal.class))).thenReturn(new XmlInvoiceDto());
        when(invoiceTemplateRepo.findAll(any(Specification.class)))
                .thenReturn(List.of(invoiceTemplate));
        when(pdfGenerator.formPdfFile(any(XmlInvoiceDto.class), anyString())).thenReturn(new byte[]{(byte)0xe0});

        byte[] file = invoiceService.createPdfFile(1L);
        assertThat(file).hasSize(1);

        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(invoiceItemRepo, times(1)).getItemsSumByInvoiceId(anyLong());
        verify(invoiceItemRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceItemMapper, times(1)).invoiceItemListToXmlListInvoiceItemDtoList(anyList());
        verify(invoiceMapper, times(1)).invoiceToXmlInvoiceDto(any(Invoice.class), any(XmlInvoiceItemsDto.class),
                any(BigDecimal.class));
        verify(invoiceTemplateRepo, times(1)).findAll(any(Specification.class));
        verify(pdfGenerator, times(1)).formPdfFile(any(XmlInvoiceDto.class), anyString());

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(invoiceMapper);
        verifyNoMoreInteractions(invoiceItemMapper);
        verifyNoMoreInteractions(invoiceTemplateRepo);
        verifyNoMoreInteractions(pdfGenerator);
    }
    @Test
    void createPdfFile_Should_Use_First_Found_Template() throws IOException {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(invoiceItemRepo.findAll(any(Specification.class))).thenReturn(List.of(new InvoiceItem()));
        when(invoiceItemMapper.invoiceItemListToXmlListInvoiceItemDtoList(anyList()))
                .thenReturn(List.of(new XmlListInvoiceItemDto()));
        when(invoiceItemRepo.getItemsSumByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(32));
        when(invoiceMapper.invoiceToXmlInvoiceDto(any(Invoice.class), any(XmlInvoiceItemsDto.class),
                any(BigDecimal.class))).thenReturn(new XmlInvoiceDto());
        when(invoiceTemplateRepo.findAll(any(Specification.class)))
                .thenReturn(List.of());
        when(invoiceTemplateRepo.findAll()).thenReturn(List.of(invoiceTemplate));
        when(pdfGenerator.formPdfFile(any(XmlInvoiceDto.class), anyString())).thenReturn(new byte[]{(byte)0xe0});

        byte[] file = invoiceService.createPdfFile(1L);
        assertThat(file).hasSize(1);

        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(invoiceItemRepo, times(1)).getItemsSumByInvoiceId(anyLong());
        verify(invoiceItemRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceItemMapper, times(1)).invoiceItemListToXmlListInvoiceItemDtoList(anyList());
        verify(invoiceMapper, times(1)).invoiceToXmlInvoiceDto(any(Invoice.class), any(XmlInvoiceItemsDto.class),
                any(BigDecimal.class));
        verify(invoiceTemplateRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceTemplateRepo, times(1)).findAll();
        verify(pdfGenerator, times(1)).formPdfFile(any(XmlInvoiceDto.class), anyString());

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(invoiceMapper);
        verifyNoMoreInteractions(invoiceItemMapper);
        verifyNoMoreInteractions(invoiceTemplateRepo);
        verifyNoMoreInteractions(pdfGenerator);
    }
    @Test
    void createPdfFile_Should_Throw_EntityNotFoundException() {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> invoiceService.createPdfFile(1L));

        verify(invoiceRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceRepo);
    }
    @Test
    void createPdfFile_Should_Throw_IOException() throws IOException {
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(invoiceItemRepo.findAll(any(Specification.class))).thenReturn(List.of(new InvoiceItem()));
        when(invoiceItemMapper.invoiceItemListToXmlListInvoiceItemDtoList(anyList()))
                .thenReturn(List.of(new XmlListInvoiceItemDto()));
        when(invoiceItemRepo.getItemsSumByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(32));
        when(invoiceMapper.invoiceToXmlInvoiceDto(any(Invoice.class), any(XmlInvoiceItemsDto.class),
                any(BigDecimal.class))).thenReturn(new XmlInvoiceDto());
        when(invoiceTemplateRepo.findAll(any(Specification.class)))
                .thenReturn(List.of());
        when(invoiceTemplateRepo.findAll()).thenReturn(List.of(invoiceTemplate));
        doThrow(IOException.class).when(pdfGenerator).formPdfFile(any(XmlInvoiceDto.class), anyString());

        byte[] file = invoiceService.createPdfFile(1L);
        assertThat(file).hasSize(0);

        verify(invoiceRepo, times(1)).findById(anyLong());
        verify(invoiceItemRepo, times(1)).getItemsSumByInvoiceId(anyLong());
        verify(invoiceItemRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceItemMapper, times(1)).invoiceItemListToXmlListInvoiceItemDtoList(anyList());
        verify(invoiceMapper, times(1)).invoiceToXmlInvoiceDto(any(Invoice.class), any(XmlInvoiceItemsDto.class),
                any(BigDecimal.class));
        verify(invoiceTemplateRepo, times(1)).findAll(any(Specification.class));
        verify(invoiceTemplateRepo, times(1)).findAll();
        verify(pdfGenerator, times(1)).formPdfFile(any(XmlInvoiceDto.class), anyString());

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(invoiceMapper);
        verifyNoMoreInteractions(invoiceItemMapper);
        verifyNoMoreInteractions(invoiceTemplateRepo);
        verifyNoMoreInteractions(pdfGenerator);
    }
    @Test
    void getInvoiceResponses() {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("page", "0");
        requestMap.put("pageSize", "1");
        requestMap.put("number", "001");
        requestMap.put("status", InvoiceStatus.PAID.toString());
        requestMap.put("date", "12.03.1900");
        requestMap.put("apartmentId", "1");
        Pageable pageable = PageRequest.of(0,1);
        Invoice invoice = new Invoice();
        invoice.setId(1L);

        when(invoiceRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl(List.of(invoice), pageable, 5));
        when(invoiceItemRepo.getItemsSumByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(32));
        when(invoiceMapper.invoiceToInvoiceResponse(any(Invoice.class), any(BigDecimal.class)))
                .thenReturn(expectedInvoiceResponse);

        Page<InvoiceResponse> invoiceResponses = invoiceService.getInvoiceResponses(requestMap);
        assertThat(invoiceResponses.getContent()).hasSize(1);
        assertThat(invoiceResponses.getContent().get(0)).usingRecursiveComparison()
                .isEqualTo(expectedInvoiceResponse);

        verify(invoiceRepo, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(invoiceItemRepo, times(1)).getItemsSumByInvoiceId(anyLong());
        verify(invoiceMapper, times(1)).invoiceToInvoiceResponse(any(Invoice.class), any(BigDecimal.class));

        verifyNoMoreInteractions(invoiceRepo);
        verifyNoMoreInteractions(invoiceItemRepo);
        verifyNoMoreInteractions(invoiceMapper);
    }
}