package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.model.owner.ApartmentOwnerDetails;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
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
        logger.info("loadUserByUsername() - Finding owner by email "+username+" for owner details");
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Owner was not found by email "+username));
        ApartmentOwnerDetails apartmentOwnerDetails = new ApartmentOwnerDetails(apartmentOwner);
        if(apartmentOwner.getStatus().equals(OwnerStatus.DISABLED)){
            apartmentOwnerDetails.setEnabled(false);
        }
        logger.info("loadUserByUsername() - Owner was found");
        return apartmentOwnerDetails;
    }
}
