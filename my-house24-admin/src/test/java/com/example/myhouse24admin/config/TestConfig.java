package com.example.myhouse24admin.config;

import com.example.myhouse24admin.configuration.awsConfiguration.S3ResourceLoader;
import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.staff.StaffDetails;
import com.example.myhouse24admin.securityFilter.RecaptchaFilter;
import com.example.myhouse24admin.service.*;
import com.example.myhouse24admin.serviceImpl.MailServiceImpl;
import com.example.myhouse24admin.util.UploadFileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

import static org.mockito.Mockito.mock;
@Configuration
public class TestConfig {
    public final static String STAFF_EMAIL = "staff.email@example.com";
    public final static String STAFF_PASSWORD = "Password123!@";
    @Bean
    public UserDetails userDetails() {
        Role role = new Role();
        role.setName("DIRECTOR");
        Staff staff = new Staff();
        staff.setId(1L);
        staff.setRole(new Role());
        staff.setEmail(STAFF_EMAIL);
        staff.setPassword(STAFF_PASSWORD);

        return new StaffDetails(staff);
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(new Locale("uk"));
        return sessionLocaleResolver;
    }

    @Bean
    public S3ResourceLoader s3ResourceLoader() {
        return new S3ResourceLoader(s3Service());
    }

    @Bean
    public S3Service s3Service() {
        return mock(S3Service.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RecaptchaService recaptchaService() {
        return mock(RecaptchaService.class);
    }

    @Bean
    public RecaptchaFilter recaptchaFilter() {
        return new RecaptchaFilter(recaptchaService());
    }

    @Bean
    public UploadFileUtil uploadFileUtil() {
        return new UploadFileUtil(s3Service());
    }
    @Bean
    public PasswordResetTokenService passwordResetTokenService(){
        return mock(PasswordResetTokenService.class);
    }
    @Bean
    public MailService mailService(){
        return mock(MailServiceImpl.class);
    }

}
