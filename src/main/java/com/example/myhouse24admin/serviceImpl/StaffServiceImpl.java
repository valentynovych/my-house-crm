package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Language;
import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.entity.StaffStatus;
import com.example.myhouse24admin.mapper.StaffMapper;
import com.example.myhouse24admin.model.staff.StaffEditRequest;
import com.example.myhouse24admin.model.staff.StaffResponse;
import com.example.myhouse24admin.repository.RoleRepo;
import com.example.myhouse24admin.repository.StaffRepo;
import com.example.myhouse24admin.service.StaffService;
import com.example.myhouse24admin.specification.StaffSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StaffServiceImpl implements StaffService {
    private final StaffRepo staffRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final StaffMapper staffMapper;
    private final Logger logger = LogManager.getLogger("serviceLogger");
    private final StaffMapper mapper = Mappers.getMapper(StaffMapper.class);

    public StaffServiceImpl(StaffRepo staffRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder, StaffMapper staffMapper) {
        this.staffRepo = staffRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.staffMapper = staffMapper;
    }

    @Override
    public void createFirstStaff() {
        logger.info("createFirstStaff() - Creating first staff");
        if (isTableEmpty()) {
            Role role = roleRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("Role not found by id 1"));
            Staff staff = staffMapper.createFirstStaff("admin@gmail.com",
                    passwordEncoder.encode("admin"),
                    "Директор", "+380991111111", role, Language.UKR);
            saveStaffInDB(staff);
        }
        logger.info("createFirstStaff() - First staff was created");
    }

    @Override
    public void addNewStaff(StaffEditRequest staffEditRequest) {
        Staff staff = Mappers.getMapper(StaffMapper.class).staffEditRequestToStaff(staffEditRequest);
        staff.setLanguage(getLanguageFromLocale());
        staffRepo.save(staff);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepo.findAll();
    }

    @Override
    public Page<StaffResponse> getStaff(int page, int pageSize, Map<String, String> searchParams) {
        Pageable pageable = PageRequest.of(page, pageSize);
        searchParams.remove("page");
        searchParams.remove("pageSize");
        StaffSpecification spec = new StaffSpecification(searchParams);
        Page<Staff> staffPage = staffRepo.findAll(spec, pageable);
        List<StaffResponse> staffResponseList = mapper.staffListToStaffResponseList(staffPage.getContent());

        Page<StaffResponse> staffResponsePage =
                new PageImpl<>(staffResponseList, pageable, staffPage.getTotalElements());
        return staffResponsePage;
    }

    @Override
    public List<String> getStatuses() {
        return Arrays.stream(StaffStatus.values()).map(Enum::name).toList();
    }

    private boolean isTableEmpty() {
        return staffRepo.count() == 0;
    }

    private void saveStaffInDB(Staff staff) {
        staffRepo.save(staff);
    }

    private Language getLanguageFromLocale() {
        String language = LocaleContextHolder.getLocale().getLanguage();
        return language.equals("en") ? Language.ENG : Language.UKR;
    }
}
