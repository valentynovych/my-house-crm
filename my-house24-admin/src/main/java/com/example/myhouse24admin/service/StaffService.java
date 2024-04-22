package com.example.myhouse24admin.service;

import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.exception.StaffIllegalStateAdminException;
import com.example.myhouse24admin.exception.StaffIllegalStateException;
import com.example.myhouse24admin.model.staff.StaffEditRequest;
import com.example.myhouse24admin.model.staff.StaffResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface StaffService {
    void createFirstStaff();

    void addNewStaff(StaffEditRequest staffEditRequest);

    List<Role> getRoles();

    Page<StaffResponse> getStaff(int page, int pageSize, Map<String, String> searchParams);

    List<String> getStatuses();

    StaffResponse getStaffById(Long staffId);

    void updateStaffById(Long staffId, StaffEditRequest staffEditRequest) throws StaffIllegalStateException, StaffIllegalStateAdminException;

    boolean deleteStaffById(Long staffId);

    Staff getCurrentStaff();

    void sendInviteToStaff(Long staffId);
}
