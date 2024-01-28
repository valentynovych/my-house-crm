package com.example.myhouse24admin.configuration;

import com.example.myhouse24admin.securityFilter.RecaptchaFilter;
import com.example.myhouse24admin.securityFilter.RoleBasedVoter;
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
    private final RoleBasedVoter roleBasedVoter;

    public SecurityConfiguration(DataSource dataSource, RecaptchaFilter recaptchaFilter, RoleBasedVoter roleBasedVoter) {
        this.dataSource = dataSource;
        this.recaptchaFilter = recaptchaFilter;
        this.roleBasedVoter = roleBasedVoter;
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
                        .requestMatchers("/assets/**","/admin/assets/**", "/admin/forgotPassword", "/admin/sentToken", "/admin/changePassword", "/admin/tokenExpired", "/admin/success").permitAll()
                        .requestMatchers("/admin/**").access(roleBasedVoter)
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/statistic",true)
                        .permitAll()
                )
                .rememberMe((rm)-> rm
                        .tokenRepository(persistentTokenRepository()))
                .logout((logout) -> logout
                        .logoutUrl("/admin/logout")
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
