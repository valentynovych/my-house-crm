package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.AboutPage;
import com.example.myhouse24admin.entity.MainPage;
import com.example.myhouse24admin.entity.MainPageBlock;
import com.example.myhouse24admin.entity.Seo;
import com.example.myhouse24admin.mapper.MainPageMapper;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageBlockRequest;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageRequest;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageResponse;
import com.example.myhouse24admin.repository.MainPageBlockRepo;
import com.example.myhouse24admin.repository.MainPageRepo;
import com.example.myhouse24admin.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class MainPageServiceImplTest {
    @Mock
    private MainPageRepo mainPageRepo;
    @Mock
    private MainPageMapper mainPageMapper;
    @Mock
    private MainPageBlockRepo mainPageBlockRepo;
    @Mock
    private UploadFileUtil uploadFileUtil;
    @InjectMocks
    private MainPageServiceImpl mainPageService;
    private static MainPageResponse expectedMainPageResponse;
    private static MainPageRequest mainPageRequest;
    private static MainPage mainPage;
    @BeforeAll
    public static void setUp(){
        expectedMainPageResponse = new MainPageResponse();
        expectedMainPageResponse.setSeoTitle("title");
        expectedMainPageResponse.setSeoKeywords("keywords");
        expectedMainPageResponse.setText("text");

        mainPageRequest = new MainPageRequest();
        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        MockMultipartFile emptyMultipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE, new byte[0]);
        mainPageRequest.setImage1(multipartFile);
        mainPageRequest.setImage2(emptyMultipartFile);
        mainPageRequest.setImage3(multipartFile);
        mainPageRequest.setIdsToDelete(List.of(1L));

        MainPageBlockRequest mainPageBlockRequest = new MainPageBlockRequest();
        mainPageBlockRequest.setId(1L);
        mainPageBlockRequest.setImage(multipartFile);
        MainPageBlockRequest mainPageBlockRequest1 = new MainPageBlockRequest();
        mainPageBlockRequest1.setId(1L);
        mainPageBlockRequest1.setImage(emptyMultipartFile);
        mainPageRequest.setMainPageBlocks(List.of(mainPageBlockRequest, mainPageBlockRequest1));

        mainPage = new MainPage();
        mainPage.setImage1("image1");
        mainPage.setImage2("image2");
        mainPage.setImage3("image3");
    }
    @Test
    void createMainPageIfNotExist_Page_Should_Not_Exist() {
        when(mainPageRepo.count()).thenReturn(0L);
        when(mainPageMapper.createMainPage(anyString(), any(Seo.class))).thenReturn(new MainPage());
        when(mainPageRepo.save(any(MainPage.class))).thenReturn(new MainPage());

        mainPageService.createMainPageIfNotExist();

        verify(mainPageRepo, times(1)).count();
        verify(mainPageMapper, times(1))
                .createMainPage(anyString(), any(Seo.class));
        verify(mainPageRepo, times(1)).save(any(MainPage.class));

        verifyNoMoreInteractions(mainPageRepo);
        verifyNoMoreInteractions(mainPageMapper);
    }
    @Test
    void createMainPageIfNotExist_Page_Should_Already_Exist() {
        when(mainPageRepo.count()).thenReturn(1L);

        mainPageService.createMainPageIfNotExist();

        verify(mainPageRepo, times(1)).count();
        verifyNoMoreInteractions(mainPageRepo);
    }
    @Test
    void getMainPageResponse_Should_Return_MainPageResponse() {
        when(mainPageRepo.findById(anyLong())).thenReturn(Optional.of(new MainPage()));
        when(mainPageBlockRepo.findAll()).thenReturn(List.of(new MainPageBlock()));
        when(mainPageMapper.mainPageToMainPageResponse(any(MainPage.class), anyList()))
                .thenReturn(expectedMainPageResponse);

        MainPageResponse mainPageResponse = mainPageService.getMainPageResponse();
        assertThat(mainPageResponse).usingRecursiveComparison().isEqualTo(expectedMainPageResponse);

        verify(mainPageRepo, times(1)).findById(anyLong());
        verify(mainPageBlockRepo, times(1)).findAll();
        verify(mainPageMapper, times(1))
                .mainPageToMainPageResponse(any(MainPage.class), anyList());

    }
    @Test
    void getMainPageResponse_Should_Throw_EntityNotFoundException() {
        when(mainPageRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> mainPageService
                .getMainPageResponse());

        verify(mainPageRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(mainPageRepo);
    }

    @Test
    void updateMainPage_Should_Update_Main_Page_And_Create_MainPageBlock() {
        when(mainPageRepo.findById(anyLong())).thenReturn(Optional.of(mainPage));

        doNothing().when(mainPageBlockRepo).deleteAllById(anyIterable());

        when(mainPageBlockRepo.findById(anyLong())).thenReturn(Optional.empty());

        when(uploadFileUtil.saveMultipartFile(any(MultipartFile.class))).thenReturn("file");
        when(mainPageMapper.createMainPageBlock(any(MainPageBlockRequest.class), anyString()))
                .thenReturn(new MainPageBlock());
        when(mainPageBlockRepo.save(any(MainPageBlock.class))).thenReturn(new MainPageBlock());

        doNothing().when(uploadFileUtil).deleteFile(anyString());

        doNothing().when(mainPageMapper).updateMainPage(any(MainPage.class),
                any(MainPageRequest.class), anyString(), anyString(), anyString());
        when(mainPageRepo.save(any(MainPage.class))).thenReturn(new MainPage());

        mainPageService.updateMainPage(mainPageRequest);

        verify(mainPageRepo, times(1)).findById(anyLong());
        verify(mainPageBlockRepo, times(1)).deleteAllById(anyIterable());
        verify(mainPageBlockRepo, times(1)).findById(anyLong());
        verify(uploadFileUtil, times(1))
                .saveMultipartFile(any(MultipartFile.class));
        verify(mainPageMapper, times(1))
                .createMainPageBlock(any(MainPageBlockRequest.class), anyString());
        verify(mainPageBlockRepo, times(1)).save(any(MainPageBlock.class));
        verify(uploadFileUtil, times(1)).deleteFile(anyString());
        verify(mainPageMapper, times(1)).updateMainPage(any(MainPage.class),
                any(MainPageRequest.class), anyString(), anyString(), anyString());
        verify(mainPageRepo, times(1)).save(any(MainPage.class));

        verifyNoMoreInteractions(mainPageRepo);
        verifyNoMoreInteractions(mainPageBlockRepo);
        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(mainPageMapper);
    }
    @Test
    void updateMainPage_Should_Update_Main_Page_And_Update_MainPageBlock() {
        when(mainPageRepo.findById(anyLong())).thenReturn(Optional.of(mainPage));

        doNothing().when(mainPageBlockRepo).deleteAllById(anyIterable());

        MainPageBlock mainPageBlock = new MainPageBlock();
        mainPageBlock.setImage("image");
        when(mainPageBlockRepo.findById(anyLong())).thenReturn(Optional.of(mainPageBlock));

        doNothing().when(uploadFileUtil).deleteFile(anyString());
        when(uploadFileUtil.saveMultipartFile(any(MultipartFile.class))).thenReturn("file");

        doNothing().when(mainPageMapper).updateMainPageBlock(any(MainPageBlock.class),
                any(MainPageBlockRequest.class), anyString());
        when(mainPageBlockRepo.save(any(MainPageBlock.class))).thenReturn(new MainPageBlock());

        doNothing().when(mainPageMapper).updateMainPage(any(MainPage.class),
                any(MainPageRequest.class), anyString(), anyString(), anyString());
        when(mainPageRepo.save(any(MainPage.class))).thenReturn(new MainPage());


        mainPageService.updateMainPage(mainPageRequest);

        verify(mainPageRepo, times(1)).findById(anyLong());
        verify(mainPageBlockRepo, times(1)).deleteAllById(anyIterable());
        verify(mainPageBlockRepo, times(2)).findById(anyLong());
        verify(uploadFileUtil, times(3)).deleteFile(anyString());
        verify(uploadFileUtil, times(3))
                .saveMultipartFile(any(MultipartFile.class));
        verify(mainPageMapper, times(2))
                .updateMainPageBlock(any(MainPageBlock.class),
                        any(MainPageBlockRequest.class), anyString());
        verify(mainPageBlockRepo, times(2)).save(any(MainPageBlock.class));
        verify(mainPageMapper, times(1)).updateMainPage(any(MainPage.class),
                any(MainPageRequest.class), anyString(), anyString(), anyString());
        verify(mainPageRepo, times(1)).save(any(MainPage.class));


        verifyNoMoreInteractions(mainPageRepo);
        verifyNoMoreInteractions(mainPageBlockRepo);
        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(mainPageMapper);
    }

    @Test
    void updateMainPage_Should_Throw_EntityNotFoundException() {
        when(mainPageRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> mainPageService
                .updateMainPage(mainPageRequest));

        verify(mainPageRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(mainPageRepo);
    }
}