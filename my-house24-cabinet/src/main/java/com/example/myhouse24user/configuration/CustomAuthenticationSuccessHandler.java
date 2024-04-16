package com.example.myhouse24user.configuration;

import com.example.myhouse24user.model.owner.ApartmentOwnerDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (apartmentOwnerHasApartment(authentication)) {
            response.sendRedirect(request.getContextPath() + "/cabinet/statistic");
        } else {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            SecurityContextHolder.setContext(context);
            failureHandler.setDefaultFailureUrl("/cabinet/login?error");
            failureHandler.onAuthenticationFailure(request, response,
                    new LockedException("Ваша квартира не зареєстрована або не активна"));
        }
    }

    private boolean apartmentOwnerHasApartment(Authentication authentication) {
        ApartmentOwnerDetails apartmentOwner = (ApartmentOwnerDetails) authentication.getPrincipal();
        return !apartmentOwner.getApartments().stream()
                .filter(apartment -> !apartment.isDeleted())
                .toList()
                .isEmpty();
    }
}
