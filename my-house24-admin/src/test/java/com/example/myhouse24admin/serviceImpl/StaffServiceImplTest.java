package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Language;
import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.entity.StaffStatus;
import com.example.myhouse24admin.exception.StaffAlreadyActiveException;
import com.example.myhouse24admin.mapper.StaffMapper;
import com.example.myhouse24admin.model.authentication.EmailRequest;
import com.example.myhouse24admin.model.staff.StaffEditRequest;
import com.example.myhouse24admin.model.staff.StaffResponse;
import com.example.myhouse24admin.repository.RoleRepo;
import com.example.myhouse24admin.repository.StaffRepo;
import com.example.myhouse24admin.service.MailService;
import com.example.myhouse24admin.service.PasswordResetTokenService;
import com.example.myhouse24admin.specification.StaffSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static com.example.myhouse24admin.config.TestConfig.STAFF_EMAIL;
import static com.example.myhouse24admin.config.TestConfig.STAFF_PASSWORD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

    @Mock
    private StaffRepo staffRepo;
    @Mock
    private RoleRepo roleRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private StaffMapper staffMapper;
    @Mock
    private MailService mailService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private PasswordResetTokenService passwordResetTokenService;
    @InjectMocks
    private StaffServiceImpl staffService;
    private static Staff staff;
    private static Role staffRole;

    @BeforeEach
    void setUp() {
        staff = new Staff();
        staffRole = new Role();
        staffRole.setId(1L);
        staffRole.setName("DIRECTOR");

        staff.setId(1L);
        staff.setFirstName("John");
        staff.setLastName("Doe");
        staff.setRole(staffRole);
        staff.setStatus(StaffStatus.ACTIVE);
        staff.setLanguage(Language.UKR);
        staff.setEmail(STAFF_EMAIL);
        staff.setPassword(STAFF_PASSWORD);
        staff.setPhoneNumber("+380991111111");
    }

    @Test
    void createFirstStaff_WhenStaffTableIsEmpty() {
        // given
        ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);

        // when
        when(staffRepo.count()).thenReturn(0L);
        when(roleRepo.findById(1L)).thenReturn(Optional.of(staffRole));
        when(staffMapper.createFirstStaff(anyString(), anyString(), anyString(), anyString(), any(Role.class), eq(Language.UKR), any(StaffStatus.class)))
                .thenReturn(staff);
        when(passwordEncoder.encode(anyString()))
                .thenReturn(STAFF_PASSWORD);

        // then
        staffService.createFirstStaff();
        verify(staffRepo).save(staffCaptor.capture());
        Staff value = staffCaptor.getValue();
        assertEquals(1L, value.getId());
        assertEquals(Language.UKR, value.getLanguage());
        assertEquals(StaffStatus.ACTIVE, value.getStatus());
    }

    @Test
    void createFirstStaff_WhenStaffTableIsNotEmpty() {
        // when
        when(staffRepo.count()).thenReturn(1L);

        // then
        staffService.createFirstStaff();
        verify(staffRepo).count();
        verify(staffMapper, never())
                .createFirstStaff(anyString(), anyString(), anyString(), anyString(), any(Role.class), any(Language.class), any(StaffStatus.class));
        clearInvocations(staffMapper);
    }

    @Test
    void createFirstStaff_WhenStaffTableIsEmptyAndRoleNotFound() {

        // when
        when(staffRepo.count()).thenReturn(0L);
        when(roleRepo.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> staffService.createFirstStaff());
    }

    @Test
    void addNewStaff_WhenCurrentLanguageIsUkr() {
        // given
        StaffEditRequest staffEditRequest = new StaffEditRequest(
                1L,
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getPassword(),
                staff.getPassword(),
                staff.getRole().getId(),
                staff.getStatus()
        );
        LocaleContextHolder.setDefaultLocale(new Locale("uk", "UA"));
        ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);

        // when
        when(staffMapper.staffEditRequestToStaff(any(StaffEditRequest.class)))
                .thenReturn(staff);
        when(passwordEncoder.encode(anyString()))
                .thenReturn(STAFF_PASSWORD + "new");

        // then
        staffService.addNewStaff(staffEditRequest);
        verify(staffMapper).staffEditRequestToStaff(any(StaffEditRequest.class));
        verify(passwordEncoder).encode(anyString());
        verify(staffRepo).save(staffCaptor.capture());

        Staff value = staffCaptor.getValue();
        assertEquals(1L, value.getId());
        assertEquals(Language.UKR, value.getLanguage());
        assertEquals(StaffStatus.NEW, value.getStatus());
        assertEquals(STAFF_PASSWORD + "new", value.getPassword());
    }

    @Test
    void addNewStaff_WhenCurrentLanguageIsEng() {
        // given
        StaffEditRequest staffEditRequest = new StaffEditRequest(
                1L,
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getPassword(),
                staff.getPassword(),
                staff.getRole().getId(),
                staff.getStatus()
        );
        LocaleContextHolder.setDefaultLocale(Locale.ENGLISH);
        ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);

        // when
        when(staffMapper.staffEditRequestToStaff(any(StaffEditRequest.class)))
                .thenReturn(staff);
        when(passwordEncoder.encode(anyString()))
                .thenReturn(STAFF_PASSWORD + "new");

        // then
        staffService.addNewStaff(staffEditRequest);
        verify(staffMapper).staffEditRequestToStaff(any(StaffEditRequest.class));
        verify(passwordEncoder).encode(anyString());
        verify(staffRepo).save(staffCaptor.capture());

        Staff value = staffCaptor.getValue();
        assertEquals(1L, value.getId());
        assertEquals(Language.ENG, value.getLanguage());
        assertEquals(StaffStatus.NEW, value.getStatus());
        assertEquals(STAFF_PASSWORD + "new", value.getPassword());
    }

    @Test
    void getRoles() {
        // given
        List<Role> roles = new ArrayList<>();
        roles.add(staffRole);

        // when
        when(roleRepo.findAll()).thenReturn(roles);

        // then
        List<Role> result = staffService.getRoles();
        verify(roleRepo).findAll();
        assertFalse(result.isEmpty());
        assertEquals(roles, result);
    }

    @Test
    void getStaff() {
        // given
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("page", "1");
        searchParams.put("pageSize", "10");
        searchParams.put("name", "John");
        Page<Staff> staffs = new PageImpl<>(List.of(staff), PageRequest.of(0, 10), 1);
        StaffResponse staffResponse = new StaffResponse(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getRole(),
                staff.getStatus());

        // when
        when(staffRepo.findAll(any(StaffSpecification.class), any(Pageable.class)))
                .thenReturn(staffs);
        when(staffMapper.staffListToStaffResponseList(anyList()))
                .thenReturn(List.of(staffResponse));

        // then
        Page<StaffResponse> result = staffService.getStaff(1, 10, searchParams);
        verify(staffRepo).findAll(any(StaffSpecification.class), any(Pageable.class));
        verify(staffMapper).staffListToStaffResponseList(anyList());

        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getStatuses() {

        // then
        List<String> result = staffService.getStatuses();
        assertFalse(result.isEmpty());
        assertEquals(StaffStatus.values().length, result.size());
    }

    @Test
    void getStaffById_WhenStaffIsFound() {
        // given
        StaffResponse staffResponse = new StaffResponse(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getRole(),
                staff.getStatus());

        // when
        when(staffRepo.findById(anyLong()))
                .thenReturn(Optional.of(staff));
        when(staffMapper.staffToStaffResponse(any(Staff.class)))
                .thenReturn(staffResponse);

        // then
        StaffResponse result = staffService.getStaffById(1L);
        verify(staffRepo).findById(eq(1L));
        verify(staffMapper).staffToStaffResponse(eq(staff));

        assertEquals(staffResponse, result);
    }

    @Test
    void getStaffById_WhenStaffIsNotFound() {

        // when
        when(staffRepo.findById(anyLong()))
                .thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class,
                () -> staffService.getStaffById(1L));
    }

    @Test
    void updateStaffById_WhenStaffPasswordIsChanged_AndIsCurrentUser() {
        clearInvocations(passwordEncoder, mailService, staffRepo);
        // given
        StaffEditRequest staffEditRequest = new StaffEditRequest(
                1L,
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getPassword(),
                staff.getPassword(),
                staff.getRole().getId(),
                staff.getStatus()
        );
        SecurityContextHolder.setContext(
                new SecurityContextImpl(new UsernamePasswordAuthenticationToken(
                        STAFF_EMAIL, STAFF_PASSWORD, List.of(new SimpleGrantedAuthority("DIRECTOR")))));

        // when
        when(staffRepo.findById(eq(1L)))
                .thenReturn(Optional.of(staff));
        when(staffRepo.findByEmail(eq(STAFF_EMAIL)))
                .thenReturn(Optional.of(staff));
        when(passwordEncoder.encode(anyString()))
                .thenReturn(STAFF_PASSWORD + "new");
        doNothing()
                .when(staffMapper).updateWithPassword(any(Staff.class), any(StaffEditRequest.class));


        // then
        staffService.updateStaffById(1L, staffEditRequest);
        verify(staffRepo).save(any(Staff.class));
        verify(passwordEncoder).encode(anyString());
        verify(staffMapper).updateWithPassword(any(Staff.class), any(StaffEditRequest.class));
    }

    @Test
    void updateStaffById_WhenStaffPasswordIsChanged_AndIsNotCurrentUser() {
        clearInvocations(passwordEncoder, mailService, staffRepo);
        // given
        Staff otherStaff = new Staff();
        otherStaff.setId(2L);
        otherStaff.setEmail(STAFF_EMAIL + "other");
        StaffEditRequest staffEditRequest = new StaffEditRequest(
                1L,
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getPassword(),
                staff.getPassword(),
                staff.getRole().getId(),
                staff.getStatus()
        );
        SecurityContextHolder.setContext(
                new SecurityContextImpl(new UsernamePasswordAuthenticationToken(
                        STAFF_EMAIL + "other", STAFF_PASSWORD, List.of(new SimpleGrantedAuthority("DIRECTOR")))));

        // when
        when(staffRepo.findById(eq(1L)))
                .thenReturn(Optional.of(staff));
        when(staffRepo.findByEmail(anyString()))
                .thenReturn(Optional.of(otherStaff));
        when(passwordEncoder.encode(anyString()))
                .thenReturn(STAFF_PASSWORD + "new");
        doNothing().when(mailService).sendNewPassword(anyString(), anyString());
        doNothing().when(staffMapper).updateWithPassword(any(Staff.class), any(StaffEditRequest.class));


        // then
        staffService.updateStaffById(1L, staffEditRequest);
        verify(staffRepo).save(any(Staff.class));
        verify(passwordEncoder).encode(anyString());
        verify(staffMapper).updateWithPassword(any(Staff.class), any(StaffEditRequest.class));
        verify(mailService).sendNewPassword(anyString(), anyString());
    }

    @Test
    void updateStaffById_WhenStaffPasswordIsNotChanged() {
        clearInvocations(passwordEncoder, mailService, staffRepo);
        // given
        StaffEditRequest staffEditRequest = new StaffEditRequest(
                1L,
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                null,
                null,
                staff.getRole().getId(),
                staff.getStatus()
        );

        // when
        when(staffRepo.findById(eq(1L)))
                .thenReturn(Optional.of(staff));
        doNothing().when(staffMapper).updateWithoutPassword(any(Staff.class), any(StaffEditRequest.class));


        // then
        staffService.updateStaffById(1L, staffEditRequest);
        verify(staffRepo).save(any(Staff.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(staffMapper).updateWithoutPassword(any(Staff.class), any(StaffEditRequest.class));
        verify(mailService, never()).sendNewPassword(anyString(), anyString());
    }

    @Test
    void deleteStaffById_WhenIsNotCurrentStaff_AndIsNotDirector() {
        // given
        staff.getRole().setName("PLUMBER");
        Staff otherStaff = new Staff();
        otherStaff.setId(2L);
        otherStaff.setEmail(STAFF_EMAIL + "other");
        SecurityContextHolder.setContext(
                new SecurityContextImpl(new UsernamePasswordAuthenticationToken(
                        STAFF_EMAIL, STAFF_PASSWORD, List.of(new SimpleGrantedAuthority("DIRECTOR")))));
        ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);
        // when
        when(staffRepo.findById(eq(1L)))
                .thenReturn(Optional.of(staff));
        when(staffRepo.findByEmail(eq(STAFF_EMAIL)))
                .thenReturn(Optional.of(otherStaff));

        boolean deleted = staffService.deleteStaffById(1L);
        // then
        verify(staffRepo).save(staffCaptor.capture());
        assertTrue(deleted);
        Staff value = staffCaptor.getValue();
        assertEquals(StaffStatus.DISABLED, value.getStatus());
        assertTrue(value.isDeleted());
    }

    @Test
    void deleteStaffById_WhenStaffIsDirector() {
        // given
        SecurityContextHolder.setContext(
                new SecurityContextImpl(new UsernamePasswordAuthenticationToken(
                        STAFF_EMAIL, STAFF_PASSWORD, List.of(new SimpleGrantedAuthority("DIRECTOR")))));

        // when
        when(staffRepo.findById(eq(1L)))
                .thenReturn(Optional.of(staff));

        boolean deleted = staffService.deleteStaffById(1L);
        // then
        verify(staffRepo, never()).save(any(Staff.class));
        verify(staffRepo, never()).findByEmail(anyString());
        assertFalse(deleted);
    }

    @Test
    void sendInviteToStaff() {
        // given
        staff.setStatus(StaffStatus.NEW);
        // when
        when(staffRepo.findById(eq(1L)))
                .thenReturn(Optional.of(staff));
        when(passwordResetTokenService.createOrUpdatePasswordResetToken(any(EmailRequest.class)))
                .thenReturn("token");
        when(httpServletRequest.getRequestURL())
                .thenReturn(new StringBuffer("http://localhost:8080"));
        doNothing().when(mailService).sendInviteToStaff(anyString(), any(Staff.class), anyString());

        // then
        staffService.sendInviteToStaff(1L);
        verify(mailService).sendInviteToStaff(eq("token"), any(Staff.class), anyString());
        verify(passwordResetTokenService).createOrUpdatePasswordResetToken(any(EmailRequest.class));
        verify(httpServletRequest).getRequestURL();
        verify(mailService).sendInviteToStaff(anyString(), any(Staff.class), anyString());
    }

    @Test
    void sendInviteToStaff_WhenInvitedStaffAlreadyActive() {
        // when
        when(staffRepo.findById(eq(1L)))
                .thenReturn(Optional.of(staff));

        // then
        assertThrows(StaffAlreadyActiveException.class, () -> staffService.sendInviteToStaff(1L));
    }
}