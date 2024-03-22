package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.entity.ApartmentOwner;
import com.example.myhouse24rest.model.auth.ApartmentOwnerDetails;
import com.example.myhouse24rest.repository.ApartmentOwnerRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);

    public UserDetailsServiceImpl(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("loadUserByUsername() - Finding owner by email {} for owner details", username);
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Owner was not found by email " + username));
        ApartmentOwnerDetails apartmentOwnerDetails = new ApartmentOwnerDetails(apartmentOwner);
        logger.info("loadUserByUsername() - Owner was found");
        return apartmentOwnerDetails;
    }
}

