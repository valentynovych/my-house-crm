package com.example.myhouse24admin.securityFilter;

import com.example.myhouse24admin.service.RecaptchaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RecaptchaFilter extends OncePerRequestFilter {
    private final RecaptchaService recaptchaService;

    public RecaptchaFilter(RecaptchaService recaptchaService) {
        this.recaptchaService = recaptchaService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String recaptcha = request.getParameter("g-recaptcha-response");
        if(recaptcha != null){
            if(!recaptchaService.isRecaptchaValid(recaptcha)){
                String url = String.valueOf(request.getRequestURL());
                response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                response.setHeader("Location", url+"?error");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
