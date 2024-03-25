package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.entity.Apartment;
import com.example.myhouse24rest.mapper.ApartmentMapper;
import com.example.myhouse24rest.model.apartment.ApartmentShortResponse;
import com.example.myhouse24rest.model.apartment.ApartmentShortResponsePage;
import com.example.myhouse24rest.repository.ApartmentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceImplTest {

    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private ApartmentMapper apartmentMapper;
    @InjectMocks
    private ApartmentServiceImpl apartmentService;

    private List<Apartment> apartments = new ArrayList<>();
    private List<ApartmentShortResponse> apartmentShortResponses = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        for (int i = 0; i < 5; i++) {
            Apartment apartment = new Apartment();
            apartment.setApartmentNumber("0000" + i);
            apartment.setArea(50.0);
            apartment.setBalance(BigDecimal.valueOf(100.0 - (i * 30)));
            apartments.add(apartment);

            ApartmentShortResponse apartmentShortResponse = new ApartmentShortResponse(
                    apartment.getId(),
                    apartment.getApartmentNumber(),
                    apartment.getBalance());
            apartmentShortResponses.add(apartmentShortResponse);

        }
    }

    @Test
    public void testGetAllApartments() {
        // Arrange
        int page = 0;
        int pageSize = 10;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");
        Pageable pageable = Pageable.ofSize(pageSize).withPage(page);

        Page<Apartment> apartmentPage = new PageImpl<>(apartments, pageable, apartments.size());
        Page<ApartmentShortResponse> responsePage =
                new ApartmentShortResponsePage(apartmentShortResponses, pageable, apartmentShortResponses.size());

        when(apartmentRepo.findAllByOwner_Email("test@example.com", pageable)).thenReturn(apartmentPage);
        when(apartmentMapper.apartmentListToApartmentShortResponseList(apartments)).thenReturn(apartmentShortResponses);

        // Act
        Page<ApartmentShortResponse> result = apartmentService.getAllApartments(page, pageSize, principal);

        // Assert
        assertEquals(5, result.getContent().size());
        assertEquals(responsePage, result);

        verify(apartmentRepo, times(1)).findAllByOwner_Email("test@example.com", pageable);
        verify(apartmentMapper, times(1)).apartmentListToApartmentShortResponseList(apartments);
    }
}