package com.example.myhouse24admin.configuration;

import com.example.myhouse24admin.authenticationSuccessHandler.CustomAuthenticationSuccessHandler;
import com.example.myhouse24admin.exceptionHandler.CustomAccessDeniedHandler;
import com.example.myhouse24admin.securityFilter.RecaptchaFilter;
import com.example.myhouse24admin.securityFilter.RoleBasedVoter;
import com.example.myhouse24admin.service.RoleService;
import com.example.myhouse24admin.serviceImpl.RoleServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration {
    private final DataSource dataSource;
    private final RecaptchaFilter recaptchaFilter;
    private final RoleBasedVoter roleBasedVoter;
    private final RoleService roleService;

    public SecurityConfiguration(DataSource dataSource, RecaptchaFilter recaptchaFilter, RoleBasedVoter roleBasedVoter, RoleService roleService) {
        this.dataSource = dataSource;
        this.recaptchaFilter = recaptchaFilter;
        this.roleBasedVoter = roleBasedVoter;
        this.roleService = roleService;
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
                        .requestMatchers("/assets/**","/pagejs/**", "/admin/forgotPassword", "/admin/sentToken", "/admin/changePassword", "/admin/tokenExpired", "/admin/success").permitAll()
                        .requestMatchers("/admin/**").access(roleBasedVoter)
                        .anyRequest().authenticated()
                )
                .exceptionHandling((ex) -> ex
                        .accessDeniedHandler(accessDeniedHandler()))
                .formLogin((form) -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll()
                )
                .rememberMe((rm)-> rm
                        .tokenRepository(persistentTokenRepository()))
                .logout((logout) -> logout
                        .logoutUrl("/admin/logout")
                        .permitAll());
//                .addFilterBefore(recaptchaFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler(roleService);
    }
}
