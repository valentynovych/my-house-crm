package com.example.myhouse24admin.config;

import com.example.myhouse24admin.configuration.awsConfiguration.S3ResourceLoader;
import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.staff.StaffDetails;
import com.example.myhouse24admin.securityFilter.RecaptchaFilter;
import com.example.myhouse24admin.service.*;
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
        staff.setRole(role);
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
    public PasswordResetTokenService passwordResetTokenService() {
        return mock(PasswordResetTokenService.class);
    }

    @Bean
    public MailService mailService() {
        return mock(MailService.class);
    }

    @Bean
    public HouseService houseService() {
        return mock(HouseService.class);
    }

    @Bean
    public SectionService sectionService() {
        return mock(SectionService.class);
    }

    @Bean
    public ApartmentService apartmentService() {
        return mock(ApartmentService.class);
    }

    @Bean
    public TariffService tariffService() {
        return mock(TariffService.class);
    }

    @Bean
    public InvoiceService invoiceService() {
        return mock(InvoiceService.class);
    }

    @Bean
    public ServicesService servicesService() {
        return mock(ServicesService.class);
    }

    @Bean
    public MeterReadingService meterReadingService() {
        return mock(MeterReadingService.class);
    }

    @Bean
    public ApartmentOwnerService apartmentOwnerService() {
        return mock(ApartmentOwnerService.class);
    }

    // for PaymentDetailsControllerTest
    @Bean
    public PaymentDetailsService paymentDetailsService() {
        return mock(PaymentDetailsService.class);
    }

    // for PaymentItemControllerTest
    @Bean
    public PaymentItemService paymentItemService() {
        return mock(PaymentItemService.class);
    }

    @Bean
    public UnitOfMeasurementService unitOfMeasurementService() {
        return mock(UnitOfMeasurementService.class);
    }

    // for StaffControllerTest
    @Bean
    public StaffService staffService() {
        return mock(StaffService.class);
    }

    // for StatisticControllerTest
    @Bean
    public StatisticService statisticService() {
        return mock(StatisticService.class);
    }

    @Bean
    public OwnerPasswordResetTokenService ownerPasswordResetTokenService(){
        return mock(OwnerPasswordResetTokenService.class);
    }
    @Bean
    public MainPageService mainPageService(){
        return mock(MainPageService.class);
    }
    @Bean
    public AboutPageService aboutPageService(){return mock(AboutPageService.class);}
}
