package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.ContactsPage;
import com.example.myhouse24admin.entity.Seo;
import com.example.myhouse24admin.mapper.ContactsPageMapper;
import com.example.myhouse24admin.model.siteManagement.contacts.ContactsPageDto;
import com.example.myhouse24admin.repository.ContactsPageRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactsPageServiceImplTest {
    @Mock
    private ContactsPageRepo contactsPageRepo;
    @Mock
    private ContactsPageMapper contactsPageMapper;
    @InjectMocks
    private ContactsPageServiceImpl contactsPageService;
    private static ContactsPageDto expectedContactsPageDto;
    @BeforeAll
    public static void setUp() {
        expectedContactsPageDto = new ContactsPageDto("title", "text", "link",
                "name", "location", "address", "phone",
                "email", "map", "seoTitle",
                "seoDescription", "keywords");

    }
    @Test
    void getContactsPageDto_Should_Return_ContactsPageDto() {
        when(contactsPageRepo.findById(anyLong())).thenReturn(Optional.of(new ContactsPage()));
        when(contactsPageMapper.contactsPageToContactsPageResponse(any(ContactsPage.class)))
                .thenReturn(expectedContactsPageDto);

        ContactsPageDto contactsPageDto = contactsPageService.getContactsPageDto();
        assertThat(contactsPageDto).usingRecursiveComparison().isEqualTo(expectedContactsPageDto);

        verify(contactsPageRepo, times(1)).findById(anyLong());
        verify(contactsPageMapper, times(1))
                .contactsPageToContactsPageResponse(any(ContactsPage.class));

        verifyNoMoreInteractions(contactsPageRepo);
        verifyNoMoreInteractions(contactsPageMapper);
    }
    @Test
    void getContactsPageDto_Should_Throw_EntityNotFoundException() {
        when(contactsPageRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> contactsPageService
                .getContactsPageDto());

        verify(contactsPageRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(contactsPageMapper);
    }


    @Test
    void updateContactsPage_Should_Update_ContactsPage() {
        when(contactsPageRepo.findById(anyLong())).thenReturn(Optional.of(new ContactsPage()));
        doNothing().when(contactsPageMapper).setContactsPage(any(ContactsPage.class), any(ContactsPageDto.class));
        when(contactsPageRepo.save(any(ContactsPage.class))).thenReturn(new ContactsPage());

        contactsPageService.updateContactsPage(expectedContactsPageDto);

        verify(contactsPageRepo, times(1)).findById(anyLong());
        verify(contactsPageMapper, times(1))
                .setContactsPage(any(ContactsPage.class), any(ContactsPageDto.class));
        verify(contactsPageRepo, times(1)).save(any(ContactsPage.class));

        verifyNoMoreInteractions(contactsPageRepo);
        verifyNoMoreInteractions(contactsPageMapper);
    }
    @Test
    void updateContactsPage_Should_Throw_EntityNotFoundException() {
        when(contactsPageRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> contactsPageService
                .updateContactsPage(expectedContactsPageDto));

        verify(contactsPageRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(contactsPageMapper);
    }

    @Test
    void createContactsPageIfNotExist_Page_Should_Not_Exist() {
        when(contactsPageRepo.count()).thenReturn(0L);
        when(contactsPageMapper.createContactsPage(anyString(), any(Seo.class)))
                .thenReturn(new ContactsPage());
        when(contactsPageRepo.save(any(ContactsPage.class))).thenReturn(new ContactsPage());

        contactsPageService.createContactsPageIfNotExist();

        verify(contactsPageRepo, times(1)).count();
        verify(contactsPageMapper, times(1))
                .createContactsPage(anyString(), any(Seo.class));
        verify(contactsPageRepo, times(1)).save(any(ContactsPage.class));

        verifyNoMoreInteractions(contactsPageRepo);
        verifyNoMoreInteractions(contactsPageMapper);
    }
    @Test
    void createContactsPageIfNotExist_Page_Should_Already_Exist() {
        when(contactsPageRepo.count()).thenReturn(1L);

        contactsPageService.createContactsPageIfNotExist();

        verify(contactsPageRepo, times(1)).count();
        verifyNoMoreInteractions(contactsPageRepo);
    }

}