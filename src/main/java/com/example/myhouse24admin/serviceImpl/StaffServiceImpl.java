package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Language;
import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.mapper.StaffMapper;
import com.example.myhouse24admin.repository.RoleRepo;
import com.example.myhouse24admin.repository.StaffRepo;
import com.example.myhouse24admin.service.StaffService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl implements StaffService {
    private final StaffRepo staffRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final StaffMapper staffMapper;
    private final Logger logger = LogManager.getLogger("serviceLogger");

    public StaffServiceImpl(StaffRepo staffRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder, StaffMapper staffMapper) {
        this.staffRepo = staffRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.staffMapper = staffMapper;
    }

    @Override
    public void createFirstStaff() {
        logger.info("createFirstStaff() - Creating first staff");
        if(isTableEmpty()){
            Role role = roleRepo.findById(1L).orElseThrow(()-> new EntityNotFoundException("Role not found by id 1"));
            Staff staff = staffMapper.createFirstStaff("admin@gmail.com",
                    passwordEncoder.encode("admin"),
                    "Директор","+380991111111",role, Language.UKR);
            saveStaffInDB(staff);
        }
        logger.info("createFirstStaff() - First staff was created");
    }
    private boolean isTableEmpty(){
        return staffRepo.count() == 0;
    }

    private void saveStaffInDB(Staff staff){
        staffRepo.save(staff);
    }
}
