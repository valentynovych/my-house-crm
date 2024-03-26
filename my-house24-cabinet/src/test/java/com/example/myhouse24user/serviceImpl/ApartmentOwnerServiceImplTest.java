package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.mapper.ApartmentMapper;
import com.example.myhouse24user.mapper.ApartmentOwnerMapper;
import com.example.myhouse24user.model.authentication.RegistrationRequest;
import com.example.myhouse24user.model.owner.*;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import com.example.myhouse24user.repository.ApartmentRepo;
import com.example.myhouse24user.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentOwnerServiceImplTest {
    @Mock
    private ApartmentOwnerRepo apartmentOwnerRepo;
    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private ApartmentOwnerMapper apartmentOwnerMapper;
    @Mock
    private ApartmentMapper apartmentMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UploadFileUtil uploadFileUtil;
    @InjectMocks
    private ApartmentOwnerServiceImpl apartmentOwnerService;
    private static ViewOwnerResponse expectedViewOwnerResponse;
    private static EditOwnerResponse expectedEditOwnerResponse;
    private static ApartmentOwner apartmentOwner;
    @BeforeAll
    public static void setUp() {
        expectedViewOwnerResponse = new ViewOwnerResponse();
        expectedViewOwnerResponse.setId(1L);
        expectedViewOwnerResponse.setFirstName("first name");
        expectedViewOwnerResponse.setLastName("last name");
        expectedViewOwnerResponse.setMiddleName("middle name");
        expectedViewOwnerResponse.setEmail("email");

        expectedEditOwnerResponse = new EditOwnerResponse(1L,"001",
                "name", "name", "name", "12.09.1990",
                OwnerStatus.NEW, "about", "phone", "viber",
                "telegram", "email", "image");

        apartmentOwner = new ApartmentOwner();
        apartmentOwner.setEmail("email");
        apartmentOwner.setOwnerId("0002");
        ApartmentOwnerDetails apartmentOwnerDetails = new ApartmentOwnerDetails(apartmentOwner);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(apartmentOwnerDetails);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void register_Should_Table_Be_Empty() {
        when(uploadFileUtil.saveDefaultOwnerImage()).thenReturn("image");
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(apartmentOwnerRepo.count()).thenReturn(0L);
        when(apartmentOwnerMapper.registrationRequestToApartmentOwner(any(RegistrationRequest.class),
                anyString(), anyString(), any(OwnerStatus.class), anyString(), anyString()))
                .thenReturn(apartmentOwner);
        when(apartmentOwnerRepo.save(any(ApartmentOwner.class)))
                .thenReturn(new ApartmentOwner());

        apartmentOwnerService.register(new RegistrationRequest("name","name",
                "name","email", "password", "confirm", true));

        verify(apartmentOwnerRepo, times(1)).count();
        verify(apartmentOwnerRepo, times(1))
                .save(any(ApartmentOwner.class));
        verify(apartmentOwnerMapper, times(1))
                .registrationRequestToApartmentOwner(any(RegistrationRequest.class),
                        anyString(), anyString(), any(OwnerStatus.class), anyString(), anyString());
        verify(uploadFileUtil, times(1)).saveDefaultOwnerImage();
        verify(passwordEncoder, times(1))
                .encode(anyString());

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }
    @Test
    void register_Should_Table_Not_Be_Empty() {
        when(uploadFileUtil.saveDefaultOwnerImage()).thenReturn("image");
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(apartmentOwnerRepo.count()).thenReturn(2L);
        when(apartmentOwnerMapper.registrationRequestToApartmentOwner(any(RegistrationRequest.class),
                anyString(), anyString(), any(OwnerStatus.class), anyString(), anyString()))
                .thenReturn(apartmentOwner);
        when(apartmentOwnerRepo.save(any(ApartmentOwner.class)))
                .thenReturn(new ApartmentOwner());
        when(apartmentOwnerRepo.findLast())
                .thenReturn(apartmentOwner);

        apartmentOwnerService.register(new RegistrationRequest("name","name",
                "name","email", "password", "confirm", true));

        verify(apartmentOwnerRepo, times(1)).count();
        verify(apartmentOwnerRepo, times(1)).findLast();
        verify(apartmentOwnerRepo, times(1))
                .save(any(ApartmentOwner.class));
        verify(apartmentOwnerMapper, times(1))
                .registrationRequestToApartmentOwner(any(RegistrationRequest.class),
                        anyString(), anyString(), any(OwnerStatus.class), anyString(), anyString());
        verify(uploadFileUtil, times(1)).saveDefaultOwnerImage();
        verify(passwordEncoder, times(1))
                .encode(anyString());

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }

    @Test
    void findApartmentOwnerByEmail_Should_Return_ApartmentOwner() {
        when(apartmentOwnerRepo.findByEmail(anyString()))
                .thenReturn(Optional.of(apartmentOwner));

        ApartmentOwner gotApartmentOwner = apartmentOwnerService.findApartmentOwnerByEmail("email");
        assertThat(gotApartmentOwner).usingRecursiveComparison().isEqualTo(apartmentOwner);

        verify(apartmentOwnerRepo, times(1)).findByEmail(anyString());

        verifyNoMoreInteractions(apartmentOwnerRepo);
    }

    @Test
    void findApartmentOwnerByEmail_Should_Return_EntityNotFoundException() {
        when(apartmentOwnerRepo.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> apartmentOwnerService.findApartmentOwnerByEmail("email"));

        verify(apartmentOwnerRepo, times(1)).findByEmail(anyString());

        verifyNoMoreInteractions(apartmentOwnerRepo);
    }
    @Test
    void getViewOwnerResponse_Should_Return_ViewOwnerResponse() {
        when(apartmentOwnerRepo.findByEmail(anyString()))
                .thenReturn(Optional.of(new ApartmentOwner()));
        when(apartmentRepo.findAll(any(Specification.class))).thenReturn(List.of(new Apartment()));
        when(apartmentMapper.apartmentListToApartmentResponseList(anyList()))
                .thenReturn(List.of(new ApartmentResponse()));
        when(apartmentOwnerMapper.ownerToViewOwnerResponse(any(ApartmentOwner.class), anyList()))
                .thenReturn(expectedViewOwnerResponse);



        ViewOwnerResponse viewOwnerResponse = apartmentOwnerService.getViewOwnerResponse();
        assertThat(viewOwnerResponse).usingRecursiveComparison().isEqualTo(expectedViewOwnerResponse);

        verify(apartmentOwnerRepo, times(1)).findByEmail(anyString());
        verify(apartmentRepo, times(1)).findAll(any(Specification.class));
        verify(apartmentMapper, times(1))
                .apartmentListToApartmentResponseList(anyList());
        verify(apartmentOwnerMapper, times(1))
                .ownerToViewOwnerResponse(any(ApartmentOwner.class), anyList());

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(apartmentMapper);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }
    @Test
    void getViewOwnerResponse_Should_Throw_EntityNotFoundException() {
        when(apartmentOwnerRepo.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> apartmentOwnerService.getViewOwnerResponse());

        verify(apartmentOwnerRepo, times(1)).findByEmail(anyString());

        verifyNoMoreInteractions(apartmentOwnerRepo);
    }

    @Test
    void getEditOwnerResponse_Should_Return_EditOwnerResponse() {
        when(apartmentOwnerRepo.findByEmail(anyString()))
                .thenReturn(Optional.of(new ApartmentOwner()));
        when(apartmentOwnerMapper.ownerToEditOwnerResponse(any(ApartmentOwner.class)))
                .thenReturn(expectedEditOwnerResponse);

        EditOwnerResponse editOwnerResponse = apartmentOwnerService.getEditOwnerResponse();
        assertThat(editOwnerResponse).usingRecursiveComparison().isEqualTo(expectedEditOwnerResponse);

        verify(apartmentOwnerRepo, times(1)).findByEmail(anyString());
        verify(apartmentOwnerMapper, times(1))
                .ownerToEditOwnerResponse(any(ApartmentOwner.class));

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }

    @Test
    void getEditOwnerResponse_Should_Throw_EntityNotFoundException() {
        when(apartmentOwnerRepo.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> apartmentOwnerService.getEditOwnerResponse());

        verify(apartmentOwnerRepo, times(1)).findByEmail(anyString());

        verifyNoMoreInteractions(apartmentOwnerRepo);
    }

    @Test
    void updateProfile_Should_Update_With_Password() {
        when(apartmentOwnerRepo.findById(anyLong()))
                .thenReturn(Optional.of(new ApartmentOwner()));
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        doNothing().when(apartmentOwnerMapper)
                .setApartmentOwnerWithPassword(any(ApartmentOwner.class),
                        any(ApartmentOwnerRequest.class), anyString());

        when(uploadFileUtil.saveMultipartFile(any(MultipartFile.class))).thenReturn("image");
        when(apartmentOwnerRepo.save(any(ApartmentOwner.class)))
                .thenReturn(new ApartmentOwner());

        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        ApartmentOwnerRequest apartmentOwnerRequest = new ApartmentOwnerRequest(1L, "name",
                "name", "name", "12.09.1990", OwnerStatus.NEW,
                "about", "+380992401786","+380992401786",
                "@telegram", "email@gmail.com", "password",
                "password");

        apartmentOwnerService.updateProfile(apartmentOwnerRequest, multipartFile);

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verify(apartmentOwnerRepo, times(1))
                .save(any(ApartmentOwner.class));
        verify(apartmentOwnerMapper, times(1))
                .setApartmentOwnerWithPassword(any(ApartmentOwner.class),
                        any(ApartmentOwnerRequest.class), anyString());
        verify(uploadFileUtil, times(1))
                .saveMultipartFile(any(MultipartFile.class));
        verify(passwordEncoder, times(1))
                .encode(anyString());

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }

    @Test
    void updateProfile_Should_Update_Without_Password() {
        when(apartmentOwnerRepo.findById(anyLong()))
                .thenReturn(Optional.of(new ApartmentOwner()));
        doNothing().when(apartmentOwnerMapper)
                .setApartmentOwnerWithoutPassword(any(ApartmentOwner.class),
                        any(ApartmentOwnerRequest.class));

        when(uploadFileUtil.saveMultipartFile(any(MultipartFile.class))).thenReturn("image");
        when(apartmentOwnerRepo.save(any(ApartmentOwner.class)))
                .thenReturn(new ApartmentOwner());

        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        ApartmentOwnerRequest apartmentOwnerRequest = new ApartmentOwnerRequest(1L, "name",
                "name", "name", "12.09.1990", OwnerStatus.NEW,
                "about", "+380992401786","+380992401786",
                "@telegram", "email@gmail.com", "",
                "password");

        apartmentOwnerService.updateProfile(apartmentOwnerRequest, multipartFile);

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verify(apartmentOwnerRepo, times(1))
                .save(any(ApartmentOwner.class));
        verify(apartmentOwnerMapper, times(1))
                .setApartmentOwnerWithoutPassword(any(ApartmentOwner.class),
                        any(ApartmentOwnerRequest.class));
        verify(uploadFileUtil, times(1))
                .saveMultipartFile(any(MultipartFile.class));

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }
}