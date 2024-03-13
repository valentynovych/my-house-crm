package com.example.myhouse24user.configuration;

import com.example.myhouse24user.securityFilter.RecaptchaFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/assets/**","/pagejs/**", "/cabinet/forgotPassword", "/cabinet/sentToken", "/cabinet/changePassword", "/cabinet/tokenExpired", "/cabinet/success").permitAll()
                        .requestMatchers("/cabinet/**").hasRole("OWNER")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/cabinet/login")
                        .loginProcessingUrl("/cabinet/login")
                        .defaultSuccessUrl("/cabinet/statistic",true)
                        .permitAll()
                )
                .rememberMe((rm)-> rm
                        .tokenRepository(persistentTokenRepository()))
                .logout((logout) -> logout
                        .logoutUrl("/cabinet/logout")
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
