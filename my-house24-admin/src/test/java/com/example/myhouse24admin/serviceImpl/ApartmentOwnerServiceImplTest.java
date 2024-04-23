package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.OwnerStatus;
import com.example.myhouse24admin.mapper.ApartmentMapper;
import com.example.myhouse24admin.mapper.ApartmentOwnerMapper;
import com.example.myhouse24admin.model.apartmentOwner.*;
import com.example.myhouse24admin.model.invoices.OwnerNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.service.MailService;
import com.example.myhouse24admin.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private MailService mailService;
    @Mock
    private UploadFileUtil uploadFileUtil;
    @InjectMocks
    private ApartmentOwnerServiceImpl apartmentOwnerService;
    private static CreateApartmentOwnerRequest createApartmentOwnerRequest;
    private static EditApartmentOwnerRequest editApartmentOwnerRequest;
    private static MockMultipartFile multipartFile;
    private static TableApartmentOwnerResponse tableApartmentOwnerResponse;
    @BeforeAll
    public static void setUp(){
        createApartmentOwnerRequest = new CreateApartmentOwnerRequest("name",
                "name", "name", "date", OwnerStatus.NEW,
                "about","phone", "viber",
                "telegram", "email", "password",
                "confirmPassword");

        editApartmentOwnerRequest = new EditApartmentOwnerRequest("name",
                "name","name", "date", OwnerStatus.NEW,
                "about", "phone", "viber",
                "telegram", "email", "",
                "confirmPassword");

        multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());

        tableApartmentOwnerResponse = new TableApartmentOwnerResponse();
        tableApartmentOwnerResponse.setEmail("email");
        tableApartmentOwnerResponse.setOwnerId("0001");
    }
    @Test
    void createApartmentOwner_Should_Save_Default_Avatar_And_Create_First_OwnerId() {
        mockForCreateApartmentOwner();
        when(uploadFileUtil.saveDefaultOwnerImage()).thenReturn("defaultImage");
        when(apartmentOwnerRepo.count()).thenReturn(0L);

        apartmentOwnerService.createApartmentOwner(createApartmentOwnerRequest,null);

        verify(uploadFileUtil, times(1)).saveDefaultOwnerImage();

        verifyForCreateApartmentOwner();
    }
    @Test
    void createApartmentOwner_Should_Save_New_Avatar_And_Create_Second_OwnerId() {
        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setOwnerId("00001");
        mockForCreateApartmentOwner();
        when(uploadFileUtil.saveMultipartFile(any(MultipartFile.class))).thenReturn("image");
        when(apartmentOwnerRepo.count()).thenReturn(1L);
        when(apartmentOwnerRepo.findLast()).thenReturn(apartmentOwner);

        apartmentOwnerService.createApartmentOwner(createApartmentOwnerRequest,multipartFile);

        verify(uploadFileUtil, times(1)).saveMultipartFile(any(MultipartFile.class));

        verifyForCreateApartmentOwner();
    }
    private void mockForCreateApartmentOwner(){
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(apartmentOwnerMapper.apartmentOwnerRequestToApartmentOwner(any(CreateApartmentOwnerRequest.class),
                anyString(), anyString(), anyString())).thenReturn(new ApartmentOwner());
        when(apartmentOwnerRepo.save(any(ApartmentOwner.class))).thenReturn(new ApartmentOwner());
    }
    private void verifyForCreateApartmentOwner(){
        verify(apartmentOwnerRepo, times(1)).count();
        verify(apartmentOwnerMapper, times(1))
                .apartmentOwnerRequestToApartmentOwner(any(CreateApartmentOwnerRequest.class),
                        anyString(), anyString(), anyString());
        verify(apartmentOwnerRepo, times(1)).save(any(ApartmentOwner.class));

        verifyNoMoreInteractions(uploadFileUtil);
        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }
    @Test
    void getApartmentOwnerResponse_Should_Return_ApartmentOwnerResponse() {
        ApartmentOwnerResponse expectedApartmentOwnerResponse = new ApartmentOwnerResponse("00001",
                "name", "name", "name", "date",
                OwnerStatus.ACTIVE, "about", "phone",
                "viber", "telegram", "email","image");

        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.of(new ApartmentOwner()));
        when(apartmentOwnerMapper.apartmentOwnerToApartmentOwnerResponse(any(ApartmentOwner.class)))
                .thenReturn(expectedApartmentOwnerResponse);

        ApartmentOwnerResponse apartmentOwnerResponse = apartmentOwnerService
                .getApartmentOwnerResponse(1L);
        assertThat(apartmentOwnerResponse).usingRecursiveComparison().isEqualTo(expectedApartmentOwnerResponse);

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verify(apartmentOwnerMapper, times(1))
                .apartmentOwnerToApartmentOwnerResponse(any(ApartmentOwner.class));

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }

    @Test
    void getApartmentOwnerResponse_Should_Throw_EntityNotFoundException() {
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> apartmentOwnerService
                .getApartmentOwnerResponse(1L));

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(apartmentOwnerRepo);
    }
    @Test
    void updateApartmentOwner_Should_Update_Owner_Without_Password() {
        mockForUpdateApartmentOwner();
        doNothing().when(apartmentOwnerMapper).setApartmentOwnerWithoutPassword(any(ApartmentOwner.class),
                any(EditApartmentOwnerRequest.class));

        apartmentOwnerService.updateApartmentOwner(editApartmentOwnerRequest, 1L, multipartFile);

        verify(apartmentOwnerMapper, times(1))
                .setApartmentOwnerWithoutPassword(any(ApartmentOwner.class),
                        any(EditApartmentOwnerRequest.class));
        verifyForUpdateApartmentOwner();
    }
    @Test
    void updateApartmentOwner_Should_Update_Owner_With_Password() {
        mockForUpdateApartmentOwner();
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doNothing().when(apartmentOwnerMapper).setApartmentOwnerWithPassword(any(ApartmentOwner.class),
                any(EditApartmentOwnerRequest.class), anyString());

        EditApartmentOwnerRequest editApartmentOwnerRequest1 = new EditApartmentOwnerRequest("name",
                "name","name", "date", OwnerStatus.NEW,
                "about", "phone", "viber",
                "telegram", "email", "password",
                "confirmPassword");
        apartmentOwnerService.updateApartmentOwner(editApartmentOwnerRequest1, 1L, multipartFile);

        verify(apartmentOwnerMapper, times(1))
                .setApartmentOwnerWithPassword(any(ApartmentOwner.class),
                        any(EditApartmentOwnerRequest.class), anyString());
        verifyForUpdateApartmentOwner();
        verifyNoMoreInteractions(passwordEncoder);
    }
    @Test
    void updateApartmentOwner_Should_Throw_EntityNotFoundException() {
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> apartmentOwnerService
                .updateApartmentOwner(editApartmentOwnerRequest, 1L, multipartFile));

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(apartmentOwnerRepo);
    }

    private void mockForUpdateApartmentOwner(){
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.of(new ApartmentOwner()));
        when(uploadFileUtil.saveMultipartFile(any(MultipartFile.class))).thenReturn("image");
        when(apartmentOwnerRepo.save(any(ApartmentOwner.class))).thenReturn(new ApartmentOwner());
    }
    private void verifyForUpdateApartmentOwner(){
        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verify(uploadFileUtil, times(1))
                .saveMultipartFile(any(MultipartFile.class));
        verify(apartmentOwnerRepo, times(1)).save(any(ApartmentOwner.class));

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(apartmentOwnerMapper);
        verifyNoMoreInteractions(uploadFileUtil);
    }

    @Test
    void getApartmentOwnerResponsesForTable_Debt_In_Filter_Is_True() {
        FilterRequest filterRequest = new FilterRequest("0001", "name",
                "phone", "email", 1L, "apartment",
                "12.03.1990", OwnerStatus.NEW, true);
        mockForGetApartmentOwnerResponsesForTable();


        Page<TableApartmentOwnerResponse> tableApartmentOwnerResponses = apartmentOwnerService
                .getApartmentOwnerResponsesForTable(0, 1, filterRequest);

        assertThat(tableApartmentOwnerResponses.getContent()).hasSize(1);
        assertThat(tableApartmentOwnerResponses.getContent().get(0))
                .usingRecursiveComparison().isEqualTo(tableApartmentOwnerResponse);

        verifyForGetApartmentOwnerResponsesForTable();
    }
    @Test
    void getApartmentOwnerResponsesForTable_Debt_In_Filter_Is_False() {
        FilterRequest filterRequest = new FilterRequest("0001", "name",
                "phone", "email", 1L, "apartment",
                "12.03.1990", OwnerStatus.NEW, false);
        mockForGetApartmentOwnerResponsesForTable();


        Page<TableApartmentOwnerResponse> tableApartmentOwnerResponses = apartmentOwnerService
                .getApartmentOwnerResponsesForTable(0, 1, filterRequest);

        assertThat(tableApartmentOwnerResponses.getContent()).hasSize(1);
        assertThat(tableApartmentOwnerResponses.getContent().get(0))
                .usingRecursiveComparison().isEqualTo(tableApartmentOwnerResponse);

        verifyForGetApartmentOwnerResponsesForTable();
    }

    private void mockForGetApartmentOwnerResponsesForTable(){
        Pageable pageable = PageRequest.of(0, 1);

        when(apartmentOwnerRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new ApartmentOwner()), pageable, 5));
        when(apartmentRepo.findAll(any(Specification.class))).thenReturn(List.of(new Apartment()));
        when(apartmentMapper.apartmentListToHouseApartmentResponseList(anyList()))
                .thenReturn(List.of(new HouseApartmentResponse("house","apartment")));
        when(apartmentOwnerMapper.apartmentOwnerToTableApartmentOwnerResponse(any(ApartmentOwner.class),
                anyList(), anyBoolean())).thenReturn(tableApartmentOwnerResponse);
    }

    private void verifyForGetApartmentOwnerResponsesForTable(){
        verify(apartmentOwnerRepo, times(1)).findAll(any(Specification.class),
                any(Pageable.class));
        verify(apartmentRepo, times(2)).findAll(any(Specification.class));
        verify(apartmentMapper, times(1))
                .apartmentListToHouseApartmentResponseList(anyList());
        verify(apartmentOwnerMapper, times(1))
                .apartmentOwnerToTableApartmentOwnerResponse(any(ApartmentOwner.class),
                        anyList(), anyBoolean());


        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(apartmentMapper);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }

    @Test
    void deleteOwnerById_Should_Delete_Owner() {
        when(apartmentRepo.findAll(any(Specification.class))).thenReturn(List.of());
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.of(new ApartmentOwner()));
        when(apartmentOwnerRepo.save(any(ApartmentOwner.class))).thenReturn(new ApartmentOwner());

        boolean deleted = apartmentOwnerService.deleteOwnerById(1L);
        assertThat(deleted).isTrue();

        verify(apartmentRepo, times(1)).findAll(any(Specification.class));
        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verify(apartmentOwnerRepo, times(1)).save(any(ApartmentOwner.class));

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(apartmentRepo);
    }
    @Test
    void deleteOwnerById_Should_Not_Delete_Owner() {
        when(apartmentRepo.findAll(any(Specification.class))).thenReturn(List.of(new Apartment()));

        boolean deleted = apartmentOwnerService.deleteOwnerById(1L);
        assertThat(deleted).isFalse();

        verify(apartmentRepo, times(1)).findAll(any(Specification.class));
        verifyNoMoreInteractions(apartmentOwnerRepo);
    }
    @Test
    void deleteOwnerById_Should_Throw_EntityNotFoundException() {
        when(apartmentRepo.findAll(any(Specification.class))).thenReturn(List.of());
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> apartmentOwnerService
                .deleteOwnerById(1L));

        verify(apartmentRepo, times(1)).findAll(any(Specification.class));
        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(apartmentOwnerRepo);
    }
    @Test
    void getApartmentOwnerResponseForView_Should_Return_ViewApartmentOwnerResponse() {
        ViewApartmentOwnerResponse expectedViewApartmentOwnerResponse = new ViewApartmentOwnerResponse("00001",
                "name", "name", "name", "12.03.1990",
                OwnerStatus.NEW, "about", "phone", "viber",
                "telegram", "email", "avatar");
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.of(new ApartmentOwner()));
        when(apartmentOwnerMapper.apartmentOwnerToViewApartmentOwnerResponse(any(ApartmentOwner.class)))
                .thenReturn(expectedViewApartmentOwnerResponse);

        ViewApartmentOwnerResponse viewApartmentOwnerResponse = apartmentOwnerService
                .getApartmentOwnerResponseForView(1L);
        assertThat(viewApartmentOwnerResponse).usingRecursiveComparison()
                .isEqualTo(expectedViewApartmentOwnerResponse);

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verify(apartmentOwnerMapper, times(1))
                .apartmentOwnerToViewApartmentOwnerResponse(any(ApartmentOwner.class));

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }
    @Test
    void getApartmentOwnerResponseForView_Should_Throw_EntityNotFoundException() {
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> apartmentOwnerService
                .getApartmentOwnerResponse(1L));

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(apartmentOwnerRepo);
    }
    @Test
    void getShortResponseOwners() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("firstName", "lastName").ascending());
        ApartmentOwnerShortResponse apartmentOwnerShortResponse = new ApartmentOwnerShortResponse(1L,"name", "phone");
        when(apartmentOwnerRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new ApartmentOwner()), pageable, 5));
        when(apartmentOwnerMapper.apartmentOwnerListToTApartmentOwnerShortResponseList(anyList()))
                .thenReturn(List.of(apartmentOwnerShortResponse));

        Page<ApartmentOwnerShortResponse> apartmentOwnerShortResponses = apartmentOwnerService
                .getShortResponseOwners(0, 1, "name");
        assertThat(apartmentOwnerShortResponses.getContent()).hasSize(1);
        assertThat(apartmentOwnerShortResponses.getContent().get(0)).usingRecursiveComparison()
                .isEqualTo(apartmentOwnerShortResponse);

        verify(apartmentOwnerRepo, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
        verify(apartmentOwnerMapper, times(1))
                .apartmentOwnerListToTApartmentOwnerShortResponseList(anyList());

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }

    @Test
    void getOwnerNameResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        OwnerNameResponse ownerNameResponse = new OwnerNameResponse(1L, "name", false);

        when(apartmentOwnerRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new ApartmentOwner()), pageable, 10));
        when(apartmentOwnerMapper.apartmentOwnerListToOwnerNameResponseList(anyList()))
                .thenReturn(List.of(ownerNameResponse));

        Page<OwnerNameResponse> ownerNameResponsePage = apartmentOwnerService
                .getOwnerNameResponses(new SelectSearchRequest("search",1));
        assertThat(ownerNameResponsePage.getContent()).hasSize(1);
        assertThat(ownerNameResponsePage.getContent().get(0)).usingRecursiveComparison()
                .isEqualTo(ownerNameResponse);

        verify(apartmentOwnerRepo, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
        verify(apartmentOwnerMapper, times(1))
                .apartmentOwnerListToOwnerNameResponseList(anyList());

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(apartmentOwnerMapper);
    }
}