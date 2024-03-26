package com.example.serviceImpl;

import com.example.entity.AboutPage;
import com.example.entity.AdditionalGallery;
import com.example.entity.Document;
import com.example.entity.Gallery;
import com.example.mapper.AboutPageMapper;
import com.example.model.aboutPage.AboutPageResponse;
import com.example.repository.AboutPageRepo;
import com.example.repository.AdditionalGalleryRepo;
import com.example.repository.DocumentRepo;
import com.example.repository.GalleryRepo;
import com.example.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AboutPageServiceImplTest {
    @Mock
    private AboutPageRepo aboutPageRepo;
    @Mock
    private GalleryRepo galleryRepo;
    @Mock
    private AdditionalGalleryRepo additionalGalleryRepo;
    @Mock
    private DocumentRepo documentRepo;
    @Mock
    private AboutPageMapper aboutPageMapper;
    @Mock
    private UploadFileUtil uploadFileUtil;
    @InjectMocks
    private AboutPageServiceImpl aboutPageService;
    private static AboutPageResponse expectedAboutPageResponse;
    @BeforeAll
    public static void setUp(){
        expectedAboutPageResponse = new AboutPageResponse();
        expectedAboutPageResponse.setTitle("title");
        expectedAboutPageResponse.setAboutText("about text");
        expectedAboutPageResponse.setAdditionalTitle("additional title");
        expectedAboutPageResponse.setAdditionalText("additional text");
    }
    @Test
    void getAboutPageResponse_Should_Return_AboutPageResponse() {
        when(aboutPageRepo.findById(1L)).thenReturn(Optional.of(new AboutPage()));
        when(galleryRepo.findAll()).thenReturn(List.of(new Gallery()));
        when(additionalGalleryRepo.findAll()).thenReturn(List.of(new AdditionalGallery()));
        when(documentRepo.findAll()).thenReturn(List.of(new Document()));
        when(aboutPageMapper.aboutPageToAboutPageResponse(any(AboutPage.class),
                anyList(), anyList(),
                anyList()))
                .thenReturn(expectedAboutPageResponse);

        AboutPageResponse aboutPageResponse = aboutPageService.getAboutPageResponse();
        assertThat(aboutPageResponse).usingRecursiveComparison().isEqualTo(expectedAboutPageResponse);

        verify(aboutPageRepo, times(1)).findById(1L);
        verify(galleryRepo, times(1)).findAll();
        verify(additionalGalleryRepo, times(1)).findAll();
        verify(documentRepo, times(1)).findAll();

        verifyNoMoreInteractions(aboutPageRepo);
        verifyNoMoreInteractions(galleryRepo);
        verifyNoMoreInteractions(additionalGalleryRepo);
        verifyNoMoreInteractions(documentRepo);
    }
    @Test
    void getAboutPageResponse_Should_Throw_EntityNotFound() {
        when(aboutPageRepo.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> aboutPageService.getAboutPageResponse());

        verify(aboutPageRepo, times(1)).findById(1L);

        verifyNoMoreInteractions(aboutPageRepo);
    }

    @Test
    void getDocument_Should_Return_Byte_Array() {
        when(uploadFileUtil.getFileInputStreamByName(anyString())).thenReturn(new ByteArrayInputStream(new byte[]{ (byte)0xe0}));
        byte[] byteArray = aboutPageService.getDocument("doc");
        assertThat(byteArray[0]).isEqualTo((byte)0xe0);
        verify(uploadFileUtil, times(1)).getFileInputStreamByName("doc");

        verifyNoMoreInteractions(uploadFileUtil);
    }
    @Test
    void getDocument_Should_Throw_IOException() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{ (byte)0xe0});
        InputStream spyinputStream = spy(inputStream);
        when(uploadFileUtil.getFileInputStreamByName(anyString())).thenReturn(spyinputStream);
        doThrow(new IOException()).when(spyinputStream).readAllBytes();
        byte[] byteArray = aboutPageService.getDocument("doc");
        assertThat(byteArray).hasSize(0);
        verify(uploadFileUtil, times(1)).getFileInputStreamByName("doc");

        verifyNoMoreInteractions(uploadFileUtil);
    }
}