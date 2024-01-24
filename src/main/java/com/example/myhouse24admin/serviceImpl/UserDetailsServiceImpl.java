package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.staff.StaffDetails;
import com.example.myhouse24admin.repository.StaffRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final StaffRepo staffRepo;
    private final Logger logger = LogManager.getLogger("serviceLogger");
    public UserDetailsServiceImpl(StaffRepo staffRepo) {
        this.staffRepo = staffRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("loadUserByUsername() - Finding staff by email "+username+" for staff details");
        Staff staff = staffRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Staff don't exists by email "+username));
        logger.info("loadUserByUsername() - Staff was found");
        return new StaffDetails(staff);
    }
}
