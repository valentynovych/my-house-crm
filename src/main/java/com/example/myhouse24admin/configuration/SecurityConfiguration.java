package com.example.myhouse24admin.configuration;

import com.example.myhouse24admin.securityFilter.RecaptchaFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration {
    private final DataSource dataSource;
    private final RecaptchaFilter recaptchaFilter;

    public SecurityConfiguration(DataSource dataSource, RecaptchaFilter recaptchaFilter) {
        this.dataSource = dataSource;
        this.recaptchaFilter = recaptchaFilter;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((c)-> c.disable())
                .securityMatcher("/**")
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/assets/**", "/forgotPassword", "/sentToken", "/changePassword", "/tokenExpired", "/success").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/statistic",true)
                        .permitAll()
                )
                .rememberMe((rm)-> rm
                        .tokenRepository(persistentTokenRepository()))
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .permitAll())
                .addFilterBefore(recaptchaFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
}
