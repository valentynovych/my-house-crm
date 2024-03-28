package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.mapper.ApartmentMapper;
import com.example.myhouse24user.model.apartments.ApartmentShortResponse;
import com.example.myhouse24user.repository.ApartmentRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.myhouse24user.config.TestConfig.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceImplTest {

    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private ApartmentMapper apartmentMapper;
    @InjectMocks
    private ApartmentServiceImpl apartmentService;
    private List<Apartment> apartments;
    private List<ApartmentShortResponse> apartmentsShortResponses;

    @BeforeEach
    void setUp() {
        apartments = new ArrayList<>();
        apartmentsShortResponses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Apartment apartment = new Apartment();
            apartment.setId((long) i);
            apartment.setApartmentNumber("0000" + i);
            apartments.add(apartment);
            apartmentsShortResponses.add(new ApartmentShortResponse(
                    (long) i, "0000" + i));
        }
    }

    @Test
    void getOwnerApartments() {
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        when(apartmentRepo.findAllByOwner_Email(eq(USER_EMAIL), any(Pageable.class)))
                .thenReturn(new PageImpl<>(apartments, pageable, apartments.size()));
        when(apartmentMapper.apartmentListToApartmentShortResponseList(eq(apartments)))
                .thenReturn(apartmentsShortResponses);

        // then
        Page<ApartmentShortResponse> ownerApartments = apartmentService.getOwnerApartments(USER_EMAIL, 0, 10);
        assertFalse(ownerApartments.isEmpty());
        assertEquals(5, ownerApartments.getNumberOfElements());
    }

    @Test
    void findApartmentByIdAndOwner_WhenFound() {

        // when
        when(apartmentRepo.findApartmentByIdAndOwner_Email(eq(1L), eq(USER_EMAIL)))
                .thenReturn(Optional.of(apartments.get(0)));

        Apartment apartmentByIdAndOwner = apartmentService.findApartmentByIdAndOwner(1L, USER_EMAIL);

        assertNotNull(apartmentByIdAndOwner);
        assertEquals(apartments.get(0).getApartmentNumber(), apartmentByIdAndOwner.getApartmentNumber());
        assertEquals(apartments.get(0).getId(), apartmentByIdAndOwner.getId());

    }

    @Test
    void findApartmentByIdAndOwner_WhenNotFound() {

        // when
        when(apartmentRepo.findApartmentByIdAndOwner_Email(eq(1L), eq(USER_EMAIL)))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> apartmentService.findApartmentByIdAndOwner(1L, USER_EMAIL));
    }
}