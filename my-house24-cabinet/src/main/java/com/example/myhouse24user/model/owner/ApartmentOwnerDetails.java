package com.example.myhouse24user.model.owner;

import com.example.myhouse24user.entity.ApartmentOwner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ApartmentOwnerDetails implements UserDetails {
    private ApartmentOwner apartmentOwner;
    private boolean isEnabled = true;

    public ApartmentOwnerDetails(ApartmentOwner apartmentOwner) {
        this.apartmentOwner = apartmentOwner;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getFullName() {
        return apartmentOwner.getFirstName()+" "+apartmentOwner.getLastName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_OWNER"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return apartmentOwner.getPassword();
    }

    @Override
    public String getUsername() {
        return apartmentOwner.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
