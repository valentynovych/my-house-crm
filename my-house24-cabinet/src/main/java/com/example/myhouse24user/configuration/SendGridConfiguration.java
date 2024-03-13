package com.example.myhouse24user.configuration;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfiguration {
    @Value("${secret-key}")
    private String SECRET_KEY;
    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(SECRET_KEY);
    }
}
