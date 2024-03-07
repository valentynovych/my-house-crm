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
import com.example.myhouse24admin.service.StaffService;
import com.example.myhouse24admin.specification.StaffSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StaffServiceImpl implements StaffService {
    private final StaffRepo staffRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final StaffMapper staffMapper;
    private final MailService mailService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final Logger logger = LogManager.getLogger(StaffServiceImpl.class);

    public StaffServiceImpl(StaffRepo staffRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder, StaffMapper staffMapper, MailService mailService, PasswordResetTokenService passwordResetTokenService) {
        this.staffRepo = staffRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.staffMapper = staffMapper;
        this.mailService = mailService;
        this.passwordResetTokenService = passwordResetTokenService;
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
            logger.info("createFirstStaff() - First staff was created");
        } else {
            logger.info("createFirstStaff() - First staff has already been created");
        }
    }

    @Override
    public void addNewStaff(StaffEditRequest staffEditRequest) {
        logger.info("addNewStaff() -> Start adding new staff");
        Staff staff = staffMapper.staffEditRequestToStaff(staffEditRequest);
        staff.setLanguage(getLanguageFromLocale());
        staff.setStatus(StaffStatus.NEW);
        staff.setPassword(passwordEncoder.encode(staffEditRequest.password()));
        staffRepo.save(staff);
        logger.info("addNewStaff() -> New Staff is added, exit");
    }

    @Override
    public List<Role> getRoles() {
        logger.info("getRoles() -> Start find all staff roles");
        List<Role> roles = roleRepo.findAll();
        logger.info("getRoles() -> Success, return list size: " + roles.size());
        return roles;
    }

    @Override
    public Page<StaffResponse> getStaff(int page, int pageSize, Map<String, String> searchParams) {
        logger.info(String.format("getStaff() -> Start with parameters - %s", searchParams));
        Pageable pageable = PageRequest.of(page, pageSize);
        searchParams.remove("page");
        searchParams.remove("pageSize");
        StaffSpecification spec = new StaffSpecification(searchParams);
        Page<Staff> staffPage = staffRepo.findAll(spec, pageable);
        List<StaffResponse> staffResponseList = staffMapper.staffListToStaffResponseList(staffPage.getContent());
        Page<StaffResponse> staffResponsePage =
                new PageImpl<>(staffResponseList, pageable, staffPage.getTotalElements());
        logger.info(String.format("getStaff() -> Exit, return elements in page - %s", staffResponseList.size()));
        return staffResponsePage;
    }

    @Override
    public List<String> getStatuses() {
        logger.info(("getStatuses() -> Start"));
        return Arrays.stream(StaffStatus.values()).map(Enum::name).toList();
    }

    @Override
    public StaffResponse getStaffById(Long staffId) {
        logger.info("getStaffById() -> Start with id: " + staffId);
        Staff staff = findStaffById(staffId);
        StaffResponse staffResponse = staffMapper.staffToStaffResponse(staff);
        logger.info("getStaffById() -> Exit, return staff with id: " + staffResponse.id());
        return staffResponse;
    }

    @Override
    public void updateStaffById(Long staffId, StaffEditRequest staffEditRequest) {
        logger.info("updateStaffById() -> start, with id: " + staffId);
        Staff staff = findStaffById(staffId);
        if (staffEditRequest.password() != null) {
            logger.info("updateStaffById() -> Start update entity with new password");
            staffMapper.updateWithPassword(staff, staffEditRequest);
            staff.setPassword(passwordEncoder.encode(staffEditRequest.password()));
            if (!isCurrentStaff(staff)) mailService.sendNewPassword(staff.getEmail(), staffEditRequest.password());
        } else {
            logger.info("updateStaffById() -> Start update entity without password");
            staffMapper.updateWithoutPassword(staff, staffEditRequest);
        }
        staffRepo.save(staff);
        logger.info("updateStaffById() -> exit, success update Staff with id: " + staffId);
    }

    @Override
    public boolean deleteStaffById(Long staffId) {
        logger.info("deleteStaffById() -> Start with id: " + staffId);
        Staff staff = findStaffById(staffId);
        if (!staff.getRole().getName().equals("DIRECTOR")) {
            staff.setStatus(StaffStatus.DISABLED);
            staff.setDeleted(true);
            staffRepo.save(staff);
            logger.info("deleteStaffById() -> Exit, success delete staff with id: " + staffId);
            return true;
        }
        logger.info("deleteStaffById() -> Exit, failed delete staff with id: " + staffId);
        return false;
    }

    @Override
    public Staff getCurrentStaff() {
        logger.info("getCurrentStaff() -> Start");
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Staff> byEmail = staffRepo.findByEmail(name);
        Staff staff = byEmail.orElseThrow(() -> new EntityNotFoundException(String.format("Staff by email: %s, not found", name)));
        logger.info("getCurrentStaff() -> Exit, return staff with email: " + name);
        return staff;
    }

    @Override
    public void sendInviteToStaff(Long staffId) {
        Staff staffById = findStaffById(staffId);
        if (staffById.getStatus().equals(StaffStatus.ACTIVE)) {
            throw new StaffAlreadyActiveException(String.format("Staff with id: %s, have status: %s",
                    staffById.getId(), staffById.getStatus()));
        }
        String resetToken = passwordResetTokenService.createOrUpdatePasswordResetToken(new EmailRequest(staffById.getEmail()));
        mailService.sendInviteToStaff(resetToken, staffById);
    }

    private boolean isCurrentStaff(Staff staff) {
        Staff currentStaff = getCurrentStaff();
        return staff.getEmail().equals(currentStaff.getEmail());
    }

    private Staff findStaffById(Long staffId) {
        logger.info("findStaffById() -> Start with id: " + staffId);
        Optional<Staff> byId = staffRepo.findById(staffId);
        Staff staff = byId.orElseThrow(() -> {
            logger.error(String.format("Staff with id: %s not found", staffId));
            return new EntityNotFoundException(String.format("Staff with id: %s not found", staffId));
        });
        logger.info("findStaffById() -> Exit, return staff with id: " + staffId);
        return staff;
    }

    private boolean isTableEmpty() {
        return staffRepo.count() == 0;
    }

    private void saveStaffInDB(Staff staff) {
        staffRepo.save(staff);
    }

    private Language getLanguageFromLocale() {
        logger.info("getLanguageFromLocale() -> Start");
        String language = LocaleContextHolder.getLocale().getLanguage();
        logger.info("getLanguageFromLocale() -> Current locale language: " + language);
        Language language1 = language.equals("en") ? Language.ENG : Language.UKR;
        logger.info("getLanguageFromLocale() -> Return language: " + language1);
        return language1;
    }
}
