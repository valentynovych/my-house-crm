package com.example.serviceImpl;

import com.example.entity.ContactsPage;
import com.example.entity.MainPage;
import com.example.entity.MainPageBlock;
import com.example.mapper.MainPageMapper;
import com.example.model.mainPage.MainPageResponse;
import com.example.repository.ContactsPageRepo;
import com.example.repository.MainPageBlockRepo;
import com.example.repository.MainPageRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
@ExtendWith(MockitoExtension.class)
class MainPageServiceImplTest {
    @Mock
    private MainPageRepo mainPageRepo;
    @Mock
    private MainPageBlockRepo mainPageBlockRepo;
    @Mock
    private ContactsPageRepo contactsPageRepo;
    @Mock
    private MainPageMapper mainPageMapper;
    @InjectMocks
    private MainPageServiceImpl mainPageService;
    private static MainPageResponse expectedMainPageResponse;
    @BeforeAll
    public static void setUp(){
        expectedMainPageResponse = new MainPageResponse();
        expectedMainPageResponse.setTitle("title");
        expectedMainPageResponse.setText("text");
        expectedMainPageResponse.setImage1("image1");
        expectedMainPageResponse.setImage2("image2");
        expectedMainPageResponse.setImage3("image3");
    }
    @Test
    void getMainPageResponse_Should_Return_MainPageResponse() {
        when(contactsPageRepo.findById(1L)).thenReturn(Optional.of(new ContactsPage()));
        when(mainPageRepo.findById(1L)).thenReturn(Optional.of(new MainPage()));
        when(mainPageBlockRepo.findAll()).thenReturn(List.of(new MainPageBlock()));

        when(mainPageMapper.mainPageToMainPageResponse(any(MainPage.class),anyList(),any(ContactsPage.class)))
                .thenReturn(expectedMainPageResponse);

        MainPageResponse mainPageResponse = mainPageService.getMainPageResponse();
        assertThat(mainPageResponse).usingRecursiveComparison().isEqualTo(expectedMainPageResponse);

        verify(mainPageRepo, times(1)).findById(1L);
        verify(mainPageBlockRepo, times(1)).findAll();
        verify(contactsPageRepo, times(1)).findById(1L);

        verifyNoMoreInteractions(mainPageRepo);
        verifyNoMoreInteractions(mainPageBlockRepo);
        verifyNoMoreInteractions(contactsPageRepo);
    }

    @Test
    void getContactsPageResponse_ContactsPageRepo_Should_Throw_EntityNotFound() {
        when(mainPageRepo.findById(1L)).thenReturn(Optional.of(new MainPage()));
        when(mainPageBlockRepo.findAll()).thenReturn(List.of(new MainPageBlock()));
        when(contactsPageRepo.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> mainPageService.getMainPageResponse());

        verify(mainPageRepo, times(1)).findById(1L);
        verify(mainPageBlockRepo, times(1)).findAll();
        verify(contactsPageRepo, times(1)).findById(1L);

        verifyNoMoreInteractions(mainPageRepo);
        verifyNoMoreInteractions(mainPageBlockRepo);
        verifyNoMoreInteractions(contactsPageRepo);
    }
    @Test
    void getContactsPageResponse_MainPageRepo_Should_Throw_EntityNotFound() {
        when(mainPageRepo.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> mainPageService.getMainPageResponse());

        verify(mainPageRepo, times(1)).findById(1L);
        verify(mainPageBlockRepo, times(0)).findAll();
        verify(contactsPageRepo, times(0)).findById(1L);

    }

}