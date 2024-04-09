package com.example.myhouse24user.config;

import com.example.myhouse24user.configuration.awsConfiguration.S3ResourceLoader;
import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.House;
import com.example.myhouse24user.model.owner.ApartmentOwnerDetails;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import com.example.myhouse24user.securityFilter.RecaptchaFilter;
import com.example.myhouse24user.service.*;
import com.example.myhouse24user.util.UploadFileUtil;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {
    public final static String USER_EMAIL = "test.email@example.com";
    public final static String USER_PASSWORD = "test.email@example.com";

    @Bean
    public UserDetails userDetails() {
        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setId(1L);
        apartmentOwner.setEmail(USER_EMAIL);
        apartmentOwner.setPassword(USER_PASSWORD);

        Apartment apartment = new Apartment();
        apartment.setId(1L);
        House house = new House();
        house.setId(1L);
        house.setName("test_house");
        apartment.setHouse(house);
        apartmentOwner.setApartments(List.of(apartment));
        return new ApartmentOwnerDetails(apartmentOwner);
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

    // Mock Services for testing

    // For ApartmentController
    @Bean
    public ApartmentService apartmentService() {
        return mock(ApartmentService.class);
    }

    //    For AuthenticationController
    @Bean
    public OwnerPasswordResetTokenService ownerPasswordResetTokenService() {
        return mock(OwnerPasswordResetTokenService.class);
    }

    @Bean
    public MailService mailService() {
        return mock(MailService.class);
    }

    @Bean
    public ApartmentOwnerService apartmentOwnerService() {
        return mock(ApartmentOwnerService.class);
    }


    // For InvoiceController
    @Bean
    public InvoiceService invoiceService() {
        return mock(InvoiceService.class);
    }

    // For MasterRequestController
    @Bean
    public MasterRequestService masterRequestService() {
        return mock(MasterRequestService.class);
    }

    // For MessagesController
    @Bean
    public MessagesService messagesService() {
        return mock(MessagesService.class);
    }

    // For ProfileController

    // For StatisticsController
    @Bean
    public StatisticService statisticService() {
        return mock(StatisticService.class);
    }

    // For TariffController
    @Bean
    public TariffService tariffService() {
        return mock(TariffService.class);
    }

    @MockBean
    public ApartmentOwnerRepo apartmentOwnerRepo;
}
