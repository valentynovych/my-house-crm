package com.example.serviceImpl;

import com.example.entity.ContactsPage;
import com.example.mapper.ContactsPageMapper;
import com.example.model.contactsPage.ContactsPageResponse;
import com.example.repository.ContactsPageRepo;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ContactsServiceImplTest {
    @Mock
    private ContactsPageRepo contactsPageRepo;
    @Mock
    private ContactsPageMapper contactsPageMapper;
    @InjectMocks
    private ContactsServiceImpl contactsService;
    private static ContactsPageResponse expectedContactsPageResponse;
    @BeforeAll
    public static void setUp(){
        expectedContactsPageResponse = new ContactsPageResponse(
                "title", "text", "link",
                "fullName", "location", "address",
                "phoneNumber", "email", "mapCode"
        );
    }

    @Test
    void getContactsPageResponse_Should_Return_ContactsPageResponse() {
        when(contactsPageRepo.findById(1L)).thenReturn(Optional.of(new ContactsPage()));
        when(contactsPageMapper.contactsPageToContactsPageResponse(any(ContactsPage.class)))
                .thenReturn(expectedContactsPageResponse);

        ContactsPageResponse contactsPageResponse = contactsService.getContactsPageResponse();
        assertThat(contactsPageResponse).usingRecursiveComparison().isEqualTo(expectedContactsPageResponse);

        verify(contactsPageRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(contactsPageRepo);
    }
    @Test
    void getContactsPageResponse_Should_Throw_EntityNotFound() {
        when(contactsPageRepo.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> contactsService.getContactsPageResponse());

        verify(contactsPageRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(contactsPageRepo);
    }

}