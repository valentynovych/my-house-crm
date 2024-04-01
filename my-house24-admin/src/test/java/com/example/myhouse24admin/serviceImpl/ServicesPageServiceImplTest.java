package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Seo;
import com.example.myhouse24admin.entity.ServicePageBlock;
import com.example.myhouse24admin.entity.ServicesPage;
import com.example.myhouse24admin.mapper.ServicesPageMapper;
import com.example.myhouse24admin.model.siteManagement.servicesPage.SeoRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageBlockRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicesPageResponse;
import com.example.myhouse24admin.repository.ServicePageBlockRepo;
import com.example.myhouse24admin.repository.ServicesPageRepo;
import com.example.myhouse24admin.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicesPageServiceImplTest {
    @Mock
    private ServicesPageRepo servicesPageRepo;
    @Mock
    private ServicesPageMapper servicesPageMapper;
    @Mock
    private ServicePageBlockRepo servicePageBlockRepo;
    @Mock
    private UploadFileUtil uploadFileUtil;
    @InjectMocks
    private ServicesPageServiceImpl servicesPageService;
    private static ServicesPageResponse expectedServicesPageResponse;
    private static ServicePageRequest servicePageRequest;
    private static ServicePageBlock servicePageBlock;
    @BeforeAll
    public static void setUp(){
        expectedServicesPageResponse = new ServicesPageResponse(List.of(new ServicePageBlock()),
                "seo", "seo", "seo");

        servicePageRequest = new ServicePageRequest();
        servicePageRequest.setIdsToDelete(List.of(1L));
        servicePageRequest.setSeoRequest(new SeoRequest());
        ServicePageBlockRequest servicePageBlockRequest = new ServicePageBlockRequest();
        servicePageBlockRequest.setId(1L);
        servicePageRequest.setServicePageBlocks(List.of(servicePageBlockRequest));

        servicePageBlock = new ServicePageBlock();
        servicePageBlock.setImage("image");
    }

    @Test
    void createServicesPageIfNotExist_Page_Should_Not_Exist() {
        when(servicesPageRepo.count()).thenReturn(0L);
        when(servicesPageMapper.createServicesPage(any(Seo.class)))
                .thenReturn(new ServicesPage());
        when(servicesPageMapper.createFirstServicePageBlock(anyString()))
                .thenReturn(new ServicePageBlock());
        when(servicesPageRepo.save(any(ServicesPage.class))).thenReturn(new ServicesPage());
        when(servicePageBlockRepo.save(any(ServicePageBlock.class)))
                .thenReturn(new ServicePageBlock());

        servicesPageService.createServicesPageIfNotExist();

        verify(servicesPageRepo, times(1)).count();
        verify(servicesPageMapper, times(1))
                .createServicesPage(any(Seo.class));
        verify(servicesPageMapper, times(1))
                .createFirstServicePageBlock(anyString());
        verify(servicesPageRepo, times(1)).save(any(ServicesPage.class));
        verify(servicePageBlockRepo, times(1)).save(any(ServicePageBlock.class));

        verifyNoMoreInteractions(servicesPageRepo);
        verifyNoMoreInteractions(servicesPageMapper);
        verifyNoMoreInteractions(servicePageBlockRepo);
    }

    @Test
    void createServicesPageIfNotExist_Page_Should_Already_Exist() {
        when(servicesPageRepo.count()).thenReturn(4L);

        servicesPageService.createServicesPageIfNotExist();

        verify(servicesPageRepo, times(1)).count();
        verifyNoMoreInteractions(servicesPageRepo);
    }
    @Test
    void getServicesPageResponse() {
        when(servicesPageRepo.findById(anyLong())).thenReturn(Optional.of(new ServicesPage()));
        when(servicePageBlockRepo.findAll((Sort) any())).thenReturn(List.of(new ServicePageBlock()));
        when(servicesPageMapper.servicesPageToServicesPageResponse(any(ServicesPage.class), anyList()))
                .thenReturn(expectedServicesPageResponse);

        ServicesPageResponse servicesPageResponse = servicesPageService.getServicesPageResponse();
        assertThat(servicesPageResponse).usingRecursiveComparison().isEqualTo(expectedServicesPageResponse);

        verify(servicesPageRepo, times(1)).findById(anyLong());
        verify(servicePageBlockRepo, times(1)).findAll((Sort) any());
        verify(servicesPageMapper, times(1))
                .servicesPageToServicesPageResponse(any(ServicesPage.class), anyList());

        verifyNoMoreInteractions(servicesPageRepo);
        verifyNoMoreInteractions(servicePageBlockRepo);
        verifyNoMoreInteractions(servicesPageMapper);
    }

    @Test
    void getServicesPageResponse_Should_Throw_EntityNotFoundException() {
        when(servicesPageRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> servicesPageService
                .getServicesPageResponse());

        verify(servicesPageRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(servicesPageRepo);
    }

    @Test
    void updateServicesPage_Should_Create_ServicePageBlock() {
        when(servicesPageRepo.findById(anyLong())).thenReturn(Optional.of(new ServicesPage()));

        doNothing().when(servicePageBlockRepo).deleteAllById(anyIterable());

        when(servicePageBlockRepo.findById(anyLong())).thenReturn(Optional.empty());

        when(uploadFileUtil.saveMultipartFile(any(MultipartFile.class))).thenReturn("image");
        when(servicesPageMapper.createServicePageBlock(any(ServicePageBlockRequest.class),
                anyString())).thenReturn(new ServicePageBlock());
        when(servicePageBlockRepo.save(any(ServicePageBlock.class)))
                .thenReturn(new ServicePageBlock());

        doNothing().when(servicesPageMapper)
                .updateServicesPage(any(ServicesPage.class), any(SeoRequest.class));
        when(servicesPageRepo.save(any(ServicesPage.class))).thenReturn(new ServicesPage());

        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        servicePageRequest.getServicePageBlocks().get(0).setImage(multipartFile);
        servicesPageService.updateServicesPage(servicePageRequest);

        verify(servicesPageRepo, times(1)).findById(anyLong());
        verify(servicePageBlockRepo, times(1))
                .deleteAllById(anyIterable());
        verify(servicePageBlockRepo, times(1)).findById(anyLong());
        verify(uploadFileUtil, times(1))
                .saveMultipartFile(any(MultipartFile.class));
        verify(servicePageBlockRepo, times(1))
                .save(any(ServicePageBlock.class));
        verify(servicesPageMapper, times(1))
                .updateServicesPage(any(ServicesPage.class), any(SeoRequest.class));
        verify(servicesPageRepo, times(1)).save(any(ServicesPage.class));

        verifyNoMoreInteractions(servicesPageRepo);
        verifyNoMoreInteractions(servicePageBlockRepo);
        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(servicesPageMapper);
    }

    @Test
    void updateServicesPage_Should_Update_ServicePageBlock_And_Update_Image() {
        when(servicesPageRepo.findById(anyLong())).thenReturn(Optional.of(new ServicesPage()));

        doNothing().when(servicePageBlockRepo).deleteAllById(anyIterable());

        when(servicePageBlockRepo.findById(anyLong())).thenReturn(Optional.of(servicePageBlock));

        doNothing().when(uploadFileUtil).deleteFile(anyString());
        when(uploadFileUtil.saveMultipartFile(any(MultipartFile.class))).thenReturn("image");

        doNothing().when(servicesPageMapper).updateServicePageBlock(any(ServicePageBlock.class),
                any(ServicePageBlockRequest.class), anyString());
        when(servicePageBlockRepo.save(any(ServicePageBlock.class)))
                .thenReturn(new ServicePageBlock());

        doNothing().when(servicesPageMapper)
                .updateServicesPage(any(ServicesPage.class), any(SeoRequest.class));
        when(servicesPageRepo.save(any(ServicesPage.class))).thenReturn(new ServicesPage());

        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        servicePageRequest.getServicePageBlocks().get(0).setImage(multipartFile);
        servicesPageService.updateServicesPage(servicePageRequest);

        verify(servicesPageRepo, times(1)).findById(anyLong());
        verify(servicePageBlockRepo, times(1))
                .deleteAllById(anyIterable());
        verify(servicePageBlockRepo, times(1)).findById(anyLong());
        verify(uploadFileUtil, times(1))
                .deleteFile(anyString());
        verify(uploadFileUtil, times(1))
                .saveMultipartFile(any(MultipartFile.class));
        verify(servicesPageMapper, times(1))
                .updateServicePageBlock(any(ServicePageBlock.class),
                        any(ServicePageBlockRequest.class), anyString());
        verify(servicePageBlockRepo, times(1))
                .save(any(ServicePageBlock.class));
        verify(servicesPageMapper, times(1))
                .updateServicesPage(any(ServicesPage.class), any(SeoRequest.class));
        verify(servicesPageRepo, times(1))
                .save(any(ServicesPage.class));

        verifyNoMoreInteractions(servicesPageRepo);
        verifyNoMoreInteractions(servicePageBlockRepo);
        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(servicesPageMapper);
    }

    @Test
    void updateServicesPage_Should_Update_ServicePageBlock_And_Not_Update_Image() {
        when(servicesPageRepo.findById(anyLong())).thenReturn(Optional.of(new ServicesPage()));

        doNothing().when(servicePageBlockRepo).deleteAllById(anyIterable());

        when(servicePageBlockRepo.findById(anyLong())).thenReturn(Optional.of(servicePageBlock));

        doNothing().when(servicesPageMapper).updateServicePageBlock(any(ServicePageBlock.class),
                any(ServicePageBlockRequest.class), anyString());
        when(servicePageBlockRepo.save(any(ServicePageBlock.class)))
                .thenReturn(new ServicePageBlock());

        doNothing().when(servicesPageMapper)
                .updateServicesPage(any(ServicesPage.class), any(SeoRequest.class));
        when(servicesPageRepo.save(any(ServicesPage.class))).thenReturn(new ServicesPage());

        MockMultipartFile emptyMultipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,new byte[0]);
        servicePageRequest.getServicePageBlocks().get(0).setImage(emptyMultipartFile);
        servicesPageService.updateServicesPage(servicePageRequest);

        verify(servicesPageRepo, times(1)).findById(anyLong());
        verify(servicePageBlockRepo, times(1))
                .deleteAllById(anyIterable());
        verify(servicePageBlockRepo, times(1)).findById(anyLong());
        verify(servicesPageMapper, times(1))
                .updateServicePageBlock(any(ServicePageBlock.class),
                        any(ServicePageBlockRequest.class), anyString());
        verify(servicePageBlockRepo, times(1))
                .save(any(ServicePageBlock.class));
        verify(servicesPageMapper, times(1))
                .updateServicesPage(any(ServicesPage.class), any(SeoRequest.class));
        verify(servicesPageRepo, times(1))
                .save(any(ServicesPage.class));

        verifyNoMoreInteractions(servicesPageRepo);
        verifyNoMoreInteractions(servicePageBlockRepo);
        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(servicesPageMapper);
    }

    @Test
    void updateServicesPage_Should_Throw_EntityNotFoundException() {
        when(servicesPageRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> servicesPageService
                .updateServicesPage(new ServicePageRequest()));

        verify(servicesPageRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(servicesPageRepo);
    }
}