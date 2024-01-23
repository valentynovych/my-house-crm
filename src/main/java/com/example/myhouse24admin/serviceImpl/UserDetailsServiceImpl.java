package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.staff.StaffDetails;
import com.example.myhouse24admin.repository.StaffRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final StaffRepo staffRepo;

    public UserDetailsServiceImpl(StaffRepo staffRepo) {
        this.staffRepo = staffRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Staff staff = staffRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Staff don't exists by email "+username));
        return new StaffDetails(staff);
    }
}
