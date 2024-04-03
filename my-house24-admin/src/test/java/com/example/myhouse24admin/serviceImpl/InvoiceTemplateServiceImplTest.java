package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.InvoiceTemplate;
import com.example.myhouse24admin.mapper.InvoiceTemplateMapper;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateListRequest;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateRequest;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateResponse;
import com.example.myhouse24admin.repository.InvoiceTemplateRepo;
import com.example.myhouse24admin.util.UploadFileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceTemplateServiceImplTest {
    @Mock
    private InvoiceTemplateRepo invoiceTemplateRepo;
    @Mock
    private InvoiceTemplateMapper invoiceTemplateMapper;
    @Mock
    private UploadFileUtil uploadFileUtil;
    @InjectMocks
    private InvoiceTemplateServiceImpl invoiceTemplateService;
    @Test
    void getInvoiceTemplatesResponses() {
        InvoiceTemplateResponse invoiceTemplateResponse = new InvoiceTemplateResponse(1L, "name", "file", true);
        when(invoiceTemplateRepo.findAll()).thenReturn(List.of(new InvoiceTemplate()));
        when(invoiceTemplateMapper.invoiceTemplateListToInvoiceTemplateResponseList(anyList()))
                .thenReturn(List.of(invoiceTemplateResponse));

        List<InvoiceTemplateResponse> invoiceTemplateResponses = invoiceTemplateService
                .getInvoiceTemplatesResponses();
        assertThat(invoiceTemplateResponses).hasSize(1);
        assertThat(invoiceTemplateResponses.get(0)).usingRecursiveComparison()
                .isEqualTo(invoiceTemplateResponse);

        verify(invoiceTemplateRepo, times(1)).findAll();
        verify(invoiceTemplateMapper, times(1))
                .invoiceTemplateListToInvoiceTemplateResponseList(anyList());

        verifyNoMoreInteractions(invoiceTemplateRepo);
        verifyNoMoreInteractions(invoiceTemplateMapper);
    }

    @Test
    void updateTemplates() {
        InvoiceTemplate invoiceTemplate = new InvoiceTemplate();
        invoiceTemplate.setFile("file");
        when(invoiceTemplateRepo.findAllById(anyIterable()))
                .thenReturn(List.of(invoiceTemplate));
        doNothing().when(uploadFileUtil).deleteFile(anyString());
        doNothing().when(invoiceTemplateRepo).deleteAllById(anyIterable());

        when(uploadFileUtil.saveMultipartFile(any(MultipartFile.class))).thenReturn("image");
        when(invoiceTemplateMapper.invoiceTemplateRequestToInvoiceTemplate(any(InvoiceTemplateRequest.class),
                anyString())).thenReturn(new InvoiceTemplate());
        when(invoiceTemplateRepo.save(any(InvoiceTemplate.class)))
                .thenReturn(new InvoiceTemplate());

        InvoiceTemplateListRequest invoiceTemplateListRequest = new InvoiceTemplateListRequest();
        invoiceTemplateListRequest.setIdsToDelete(List.of(1L));
        InvoiceTemplateRequest invoiceTemplateRequest = new InvoiceTemplateRequest();
        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        invoiceTemplateRequest.setFile(multipartFile);
        invoiceTemplateListRequest.setInvoiceTemplates(List.of(invoiceTemplateRequest));
        invoiceTemplateService.updateTemplates(invoiceTemplateListRequest);

        verify(invoiceTemplateRepo, times(1)).findAllById(anyIterable());
        verify(uploadFileUtil, times(1)).deleteFile(anyString());
        verify(invoiceTemplateRepo, times(1))
                .deleteAllById(anyIterable());
        verify(uploadFileUtil, times(1))
                .saveMultipartFile(any(MultipartFile.class));
        verify(invoiceTemplateMapper, times(1))
                .invoiceTemplateRequestToInvoiceTemplate(any(InvoiceTemplateRequest.class),
                        anyString());
        verify(invoiceTemplateRepo, times(1))
                .save(any(InvoiceTemplate.class));

        verifyNoMoreInteractions(invoiceTemplateRepo);
        verifyNoMoreInteractions(uploadFileUtil);
    }

    @Test
    void setDefaultInvoice() {
        when(invoiceTemplateRepo.findAll(any(Specification.class)))
                .thenReturn(List.of(new InvoiceTemplate()));
        when(invoiceTemplateRepo.save(any(InvoiceTemplate.class)))
                .thenReturn(new InvoiceTemplate());
        when(invoiceTemplateRepo.findById(anyLong()))
                .thenReturn(Optional.of(new InvoiceTemplate()));

        invoiceTemplateService.setDefaultInvoice(1L);

        verify(invoiceTemplateRepo, times(1))
                .findAll(any(Specification.class));
        verify(invoiceTemplateRepo, times(2))
                .save(any(InvoiceTemplate.class));
        verify(invoiceTemplateRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(invoiceTemplateRepo);
    }

    @Test
    void getTemplateFile_Should_Return_Byte_Array() {
        when(uploadFileUtil.getFileInputStreamByName(anyString()))
                .thenReturn(new ByteArrayInputStream(new byte[]{ (byte)0xe0}));

        byte[] file = invoiceTemplateService.getTemplateFile("file");
        assertThat(file[0]).isEqualTo((byte)0xe0);

        verify(uploadFileUtil, times(1))
                .getFileInputStreamByName(anyString());
        verifyNoMoreInteractions(uploadFileUtil);
    }
    @Test
    void getTemplateFile_Should_Throw_IOException() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{ (byte)0xe0});
        InputStream spyinputStream = spy(inputStream);
        when(uploadFileUtil.getFileInputStreamByName(anyString())).thenReturn(spyinputStream);
        doThrow(new IOException()).when(spyinputStream).readAllBytes();

        byte[] byteArray = invoiceTemplateService.getTemplateFile("file");
        assertThat(byteArray).isNull();

        verify(uploadFileUtil, times(1)).getFileInputStreamByName(anyString());
        verifyNoMoreInteractions(uploadFileUtil);
    }
}