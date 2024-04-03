package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.mapper.AboutPageMapper;
import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageRequest;
import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageResponse;
import com.example.myhouse24admin.repository.AboutPageRepo;
import com.example.myhouse24admin.repository.AdditionalGalleryRepo;
import com.example.myhouse24admin.repository.DocumentRepo;
import com.example.myhouse24admin.repository.GalleryRepo;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AboutPageServiceImplTest {
    @Mock
    private AboutPageRepo aboutPageRepo;
    @Mock
    private AboutPageMapper aboutPageMapper;
    @Mock
    private GalleryRepo galleryRepo;
    @Mock
    private AdditionalGalleryRepo additionalGalleryRepo;
    @Mock
    private DocumentRepo documentRepo;
    @Mock
    private UploadFileUtil uploadFileUtil;
    @InjectMocks
    private AboutPageServiceImpl aboutPageService;
    private static AboutPageRequest aboutPageRequest;
    @BeforeAll
    public static void setUp(){
        aboutPageRequest = new AboutPageRequest();
        aboutPageRequest.setAdditionalGalleryIdsToDelete(List.of(1L));
        aboutPageRequest.setDocumentIdsToDelete(List.of(1L));
        aboutPageRequest.setGalleryIdsToDelete(List.of(1L));

        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        aboutPageRequest.setNewDocuments(List.of(multipartFile));
        aboutPageRequest.setNewImages(List.of(multipartFile));
        aboutPageRequest.setAdditionalNewImages(List.of(multipartFile));
    }

    @Test
    void createAboutPageIfNotExist_Page_Should_Not_Exist() {
        when(aboutPageRepo.count()).thenReturn(0L);
        when(aboutPageMapper.createAboutPage(anyString(), any(Seo.class))).thenReturn(new AboutPage());
        when(aboutPageRepo.save(any(AboutPage.class))).thenReturn(new AboutPage());

        aboutPageService.createAboutPageIfNotExist();

        verify(aboutPageRepo, times(1)).count();
        verify(aboutPageMapper, times(1))
                .createAboutPage(anyString(), any(Seo.class));
        verify(aboutPageRepo, times(1)).save(any(AboutPage.class));

        verifyNoMoreInteractions(aboutPageRepo);
        verifyNoMoreInteractions(aboutPageMapper);
    }
    @Test
    void createAboutPageIfNotExist_Page_Should_Already_Exist() {
        when(aboutPageRepo.count()).thenReturn(1L);

        aboutPageService.createAboutPageIfNotExist();

        verify(aboutPageRepo, times(1)).count();
        verifyNoMoreInteractions(aboutPageRepo);
    }

    @Test
    void getAboutPageResponse_Should_Return_AboutPageResponse() {
        AboutPageResponse expectedAboutPageResponse = new AboutPageResponse();
        expectedAboutPageResponse.setTitle("title");
        expectedAboutPageResponse.setAboutText("text");
        expectedAboutPageResponse.setAdditionalText("ad text");

        when(aboutPageRepo.findById(anyLong())).thenReturn(Optional.of(new AboutPage()));
        when(galleryRepo.findAll()).thenReturn(List.of(new Gallery()));
        when(additionalGalleryRepo.findAll()).thenReturn(List.of(new AdditionalGallery()));
        when(documentRepo.findAll()).thenReturn(List.of(new Document()));
        when(aboutPageMapper.aboutPageToAboutPageResponse(any(AboutPage.class), anyList(),
                anyList(), anyList())).thenReturn(expectedAboutPageResponse);

        AboutPageResponse aboutPageResponse = aboutPageService.getAboutPageResponse();
        assertThat(aboutPageResponse).usingRecursiveComparison().isEqualTo(expectedAboutPageResponse);

        verify(aboutPageRepo, times(1)).findById(anyLong());
        verify(galleryRepo, times(1)).findAll();
        verify(additionalGalleryRepo, times(1)).findAll();
        verify(documentRepo, times(1)).findAll();
        verify(aboutPageMapper, times(1))
                .aboutPageToAboutPageResponse(any(AboutPage.class), anyList(), anyList(), anyList());

        verifyNoMoreInteractions(aboutPageRepo);
        verifyNoMoreInteractions(galleryRepo);
        verifyNoMoreInteractions(additionalGalleryRepo);
        verifyNoMoreInteractions(documentRepo);
        verifyNoMoreInteractions(aboutPageMapper);
    }
    @Test
    void getAboutPageResponse_Should_Throw_EntityNotFoundException() {
        when(aboutPageRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> aboutPageService
                .getAboutPageResponse());

        verify(aboutPageRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(aboutPageRepo);
    }
    @Test
    void updateAboutPage_Should_Update_AboutPage_And_Update_Image() {
        mockForUpdateAboutPage();

        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        aboutPageRequest.setDirectorImage(multipartFile);
        aboutPageService.updateAboutPage(aboutPageRequest);

        verifyForUpdateAboutPage();
        verify(uploadFileUtil, times(4)).deleteFile(anyString());
        verify(uploadFileUtil, times(4)).saveMultipartFile(any(MultipartFile.class));

        verifyInteractionsForUpdateAboutPage();
    }
    @Test
    void updateAboutPage_Should_Update_AboutPage_And_Image_To_Update_Empty() {
        mockForUpdateAboutPage();

        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,new byte[0]);
        aboutPageRequest.setDirectorImage(multipartFile);
        aboutPageService.updateAboutPage(aboutPageRequest);

        verifyForUpdateAboutPage();
        verify(uploadFileUtil, times(3)).deleteFile(anyString());
        verify(uploadFileUtil, times(3)).saveMultipartFile(any(MultipartFile.class));

        verifyInteractionsForUpdateAboutPage();
    }
    private void mockForUpdateAboutPage(){
        AboutPage aboutPage = new AboutPage();
        aboutPage.setDirectorImage("image");
        when(aboutPageRepo.findById(anyLong())).thenReturn(Optional.of(aboutPage));

        Document document = new Document();
        document.setName("document");
        when(documentRepo.findAllById(anyIterable())).thenReturn(List.of(document));
        doNothing().when(uploadFileUtil).deleteFile(anyString());
        doNothing().when(documentRepo).deleteAllById(anyIterable());

        AdditionalGallery additionalGallery = new AdditionalGallery();
        additionalGallery.setImage("image");
        when(additionalGalleryRepo.findAllById(anyIterable())).thenReturn(List.of(additionalGallery));
        doNothing().when(additionalGalleryRepo).deleteAllById(anyIterable());

        Gallery gallery = new Gallery();
        gallery.setImage("image");
        when(galleryRepo.findAllById(anyIterable())).thenReturn(List.of(gallery));
        doNothing().when(galleryRepo).deleteAllById(anyIterable());

        when(uploadFileUtil.saveMultipartFile(any(MultipartFile.class))).thenReturn("file");
        when(documentRepo.save(any(Document.class))).thenReturn(new Document());

        when(galleryRepo.save(any(Gallery.class))).thenReturn(new Gallery());

        when(additionalGalleryRepo.save(any(AdditionalGallery.class))).thenReturn(new AdditionalGallery());

        doNothing().when(aboutPageMapper).updateAboutPage(any(AboutPage.class),
                any(AboutPageRequest.class), anyString());
        when(aboutPageRepo.save(any(AboutPage.class))).thenReturn(new AboutPage());
    }

    private void verifyForUpdateAboutPage(){
        verify(aboutPageRepo, times(1)).findById(anyLong());
        verify(documentRepo, times(1)).findAllById(anyIterable());
        verify(documentRepo, times(1)).deleteAllById(anyIterable());

        verify(additionalGalleryRepo, times(1)).findAllById(anyIterable());
        verify(additionalGalleryRepo, times(1)).deleteAllById(anyIterable());

        verify(galleryRepo, times(1)).findAllById(anyIterable());
        verify(galleryRepo, times(1)).deleteAllById(anyIterable());

        verify(documentRepo, times(1)).save(any(Document.class));

        verify(galleryRepo, times(1)).save(any(Gallery.class));
        verify(additionalGalleryRepo, times(1)).save(any(AdditionalGallery.class));

        verify(aboutPageMapper, times(1))
                .updateAboutPage(any(AboutPage.class), any(AboutPageRequest.class), anyString());
        verify(aboutPageRepo, times(1)).save(any(AboutPage.class));
    }

    private void verifyInteractionsForUpdateAboutPage(){
        verifyNoMoreInteractions(aboutPageRepo);
        verifyNoMoreInteractions(documentRepo);
        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(additionalGalleryRepo);
        verifyNoMoreInteractions(galleryRepo);
        verifyNoMoreInteractions(aboutPageMapper);
    }
    @Test
    void updateAboutPage_Should_Throw_EntityNotFoundException() {
        when(aboutPageRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> aboutPageService
                .updateAboutPage(new AboutPageRequest()));

        verify(aboutPageRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(aboutPageRepo);
    }
}