package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.entity.StaffStatus;
import com.example.myhouse24admin.exception.StaffIllegalStateAdminException;
import com.example.myhouse24admin.exception.StaffIllegalStateException;
import com.example.myhouse24admin.model.staff.StaffEditRequest;
import com.example.myhouse24admin.model.staff.StaffResponse;
import com.example.myhouse24admin.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private StaffService staffService;
    private static Staff staff;

    @BeforeEach
    void setUp() {
        clearInvocations(staffService);
        staff = new Staff();
        staff.setId(1L);
        staff.setEmail("staff.email@example.com");
        staff.setPassword("Password123!@");
        staff.setDeleted(false);
        staff.setRole(new Role());
        staff.getRole().setId(1L);
        staff.getRole().setName("DIRECTOR");
        staff.setFirstName("John");
        staff.setLastName("Doe");
        staff.setPhoneNumber("+380123456789");
        staff.setStatus(StaffStatus.ACTIVE);
    }

    @Test
    void viewAddStaff() throws Exception {
        // given
        var request = get("/admin/system-settings/staff/add")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("system/staff/add-staff"));
    }

    @Test
    void viewStaff() throws Exception {
        // given
        var request = get("/admin/system-settings/staff")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("system/staff/staff"));
    }

    @Test
    void viewEditStaff() throws Exception {
        // given
        var request = get("/admin/system-settings/staff/edit-staff/1")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("system/staff/edit-staff"));
    }

    @Test
    void testViewStaff() throws Exception {
        // given
        var request = get("/admin/system-settings/staff/view-staff/1")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("system/staff/view-staff"));
    }

    @Test
    void addNewStaff() throws Exception {
        // given
        var staffEditRequest = new StaffEditRequest(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                "+380673456789",
                staff.getEmail(),
                staff.getPassword(),
                staff.getPassword(),
                staff.getRole().getId(),
                staff.getStatus()
        );
        var request = post("/admin/system-settings/staff/add")
                .with(user(userDetails))
                .flashAttr("staffEditRequest", staffEditRequest);

        // when
        doNothing().when(staffService).addNewStaff(eq(staffEditRequest));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(staffService, times(1)).addNewStaff(eq(staffEditRequest));
    }

    @Test
    void getStaffRoles() throws Exception {
        // given
        var staffRoles = List.of(staff.getRole());
        var request = get("/admin/system-settings/staff/get-roles")
                .with(user(userDetails));

        // when
        doReturn(staffRoles)
                .when(staffService).getRoles();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {
                                "id": 1, "name": "DIRECTOR"
                            }
                        ]
                        """));

        verify(staffService, times(1)).getRoles();
    }

    @Test
    void getStaffStatuses() throws Exception {
        // given
        var staffStatuses = Arrays.stream(StaffStatus.values()).map(Enum::name).toList();
        var request = get("/admin/system-settings/staff/get-statuses")
                .with(user(userDetails));

        // when
        doReturn(staffStatuses)
                .when(staffService).getStatuses();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(staffStatuses.size()));

        verify(staffService, times(1)).getStatuses();
    }

    @Test
    void getStaff() throws Exception {
        // given
        var staffResponse = new StaffResponse(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getRole(),
                staff.getStatus()
        );
        var request = get("/admin/system-settings/staff/get-staff")
                .with(user(userDetails))
                .param("page", "0")
                .param("pageSize", "10");
        var searchParams = new HashMap<String, String>();
        searchParams.put("firstName", "John");
        var staffResponsePage = new PageImpl<>(List.of(staffResponse, staffResponse), PageRequest.of(0, 10), 2);

        // when
        doReturn(staffResponsePage)
                .when(staffService).getStaff(eq(0), eq(10), anyMap());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.size()").value(staffResponsePage.getTotalElements()));

        verify(staffService, times(1)).getStaff(eq(0), eq(10), anyMap());
    }

    @Test
    void getStaffById() throws Exception {
        // given
        var staffResponse = new StaffResponse(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getRole(),
                staff.getStatus()
        );
        var request = get("/admin/system-settings/staff/get-staff/1")
                .with(user(userDetails));

        // when
        doReturn(staffResponse)
                .when(staffService).getStaffById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "firstName": "John",
                            "lastName": "Doe",
                            "phoneNumber": "+380123456789",
                            "email": "staff.email@example.com",
                            "role": {
                                "id": 1,
                                "name": "DIRECTOR"
                                },
                            "status": "ACTIVE"
                        }
                            """));

        verify(staffService, times(1)).getStaffById(eq(1L));
    }

    @Test
    void updateStaffById_WhenRequestIsValid() throws Exception, StaffIllegalStateException, StaffIllegalStateAdminException {
        // given
        var staffEditRequest = new StaffEditRequest(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                "+380673456789",
                staff.getEmail(),
                staff.getPassword(),
                staff.getPassword(),
                staff.getRole().getId(),
                staff.getStatus()
        );
        var request = post("/admin/system-settings/staff/edit-staff/1")
                .with(user(userDetails))
                .flashAttr("staffEditRequest", staffEditRequest);

        // when
        doNothing()
                .when(staffService).updateStaffById(eq(1L), any(StaffEditRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(staffService, times(1)).updateStaffById(eq(1L), any(StaffEditRequest.class));
    }

    @Test
    void updateStaffById_WhenRequestIsNotValid() throws Exception, StaffIllegalStateException, StaffIllegalStateAdminException {
        // given
        var staffEditRequest = new StaffEditRequest(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                "",
                staff.getEmail(),
                staff.getPassword(),
                staff.getPassword(),
                staff.getRole().getId(),
                staff.getStatus()
        );
        var request = post("/admin/system-settings/staff/edit-staff/1")
                .with(user(userDetails))
                .flashAttr("staffEditRequest", staffEditRequest);

        // when
        doNothing()
                .when(staffService).updateStaffById(eq(1L), any(StaffEditRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(staffService, never()).updateStaffById(eq(1L), any(StaffEditRequest.class));
    }

    @Test
    void updateStaffById_WhenRequestIsValidAndThrowsStaffIllegalStateException() throws Exception, StaffIllegalStateException, StaffIllegalStateAdminException {
        staff.setPhoneNumber("+380500000000");
        // given
        var staffEditRequest = new StaffEditRequest(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getPassword(),
                staff.getPassword(),
                staff.getRole().getId(),
                staff.getStatus()
        );
        var request = post("/admin/system-settings/staff/edit-staff/1")
                .with(user(userDetails))
                .flashAttr("staffEditRequest", staffEditRequest);

        // when
        doThrow(new StaffIllegalStateException("Staff is not allowed to edit"))
                .when(staffService).updateStaffById(eq(1L), any(StaffEditRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isConflict());

        verify(staffService, times(1)).updateStaffById(eq(1L), any(StaffEditRequest.class));
    }

    @Test
    void updateStaffById_WhenRequestIsValidAndThrowsStaffIllegalStateAdminException() throws Exception, StaffIllegalStateException, StaffIllegalStateAdminException {
        // given
        staff.setPhoneNumber("+380500000000");
        var staffEditRequest = new StaffEditRequest(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getPassword(),
                staff.getPassword(),
                staff.getRole().getId(),
                staff.getStatus()
        );
        var request = post("/admin/system-settings/staff/edit-staff/1")
                .with(user(userDetails))
                .flashAttr("staffEditRequest", staffEditRequest);

        // when
        doThrow(new StaffIllegalStateAdminException("Admin cannot change role or status"))
                .when(staffService).updateStaffById(eq(1L), any(StaffEditRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isLocked());

        verify(staffService, times(1)).updateStaffById(eq(1L), any(StaffEditRequest.class));
    }

    @Test
    void deleteStaffById_WhenSuccessDelete() throws Exception {
        // given
        var request = delete("/admin/system-settings/staff/delete/1")
                .with(user(userDetails));

        // when
        doReturn(true)
                .when(staffService).deleteStaffById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(staffService, times(1)).deleteStaffById(eq(1L));
    }

    @Test
    void deleteStaffById_WhenDeletingIsFailed() throws Exception {
        // given
        var request = delete("/admin/system-settings/staff/delete/1")
                .with(user(userDetails));

        // when
        doReturn(false)
                .when(staffService).deleteStaffById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(staffService, times(1)).deleteStaffById(eq(1L));
    }

    @Test
    void sendInviteToStaff() throws Exception {
        // given
        var request = post("/admin/system-settings/staff/send-invite")
                .with(user(userDetails))
                .param("staffId", "1");

        // when
        doNothing()
                .when(staffService).sendInviteToStaff(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(staffService, times(1)).sendInviteToStaff(eq(1L));
    }
}