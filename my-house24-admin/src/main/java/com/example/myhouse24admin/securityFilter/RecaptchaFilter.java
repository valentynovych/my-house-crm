package com.example.myhouse24admin.securityFilter;

import com.example.myhouse24admin.service.RecaptchaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RecaptchaFilter extends OncePerRequestFilter {
    private final RecaptchaService recaptchaService;
    private final SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    public RecaptchaFilter(RecaptchaService recaptchaService) {
        this.recaptchaService = recaptchaService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String recaptcha = request.getParameter("g-recaptcha-response");
        if(recaptcha != null){
            if(!recaptchaService.isRecaptchaValid(recaptcha)){
                failureHandler.setDefaultFailureUrl(request.getRequestURL()+"?error");
                failureHandler.onAuthenticationFailure(request,response, new AuthenticationServiceException("Доведіть що ви не робот"));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
