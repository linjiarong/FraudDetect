package com.example.frauddetection.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
/**
 * Security configuration class for the Fraud Detection Service.
 * 
 * This class configures Spring Security to secure the application endpoints.
 * It defines the security filter chain and specifies the authentication and authorization rules.
 * 
 * Features:
 * - Disables CSRF protection for simplicity (not recommended for production without additional safeguards).
 * - Secures the `/actuator/shutdown` endpoint, allowing access only to users with the `ADMIN` role.
 * - Requires authentication for all other endpoints.
 * - Configures HTTP Basic authentication for simplicity.
 * 
 * Beans:
 * - `SecurityFilterChain`: Defines the security filter chain for the application.
 * 
 * Example Usage:
 * - Users with the `ADMIN` role can access the `/actuator/shutdown` endpoint to shut down the application.
 * - All other endpoints require authentication.
 * 
 * Notes:
 * - Ensure that user credentials and roles are properly configured in the application properties or a user management system.
 * - Consider enabling CSRF protection for production environments to prevent cross-site request forgery attacks.
 * 
 * Author: linjiarong
 * Date: 2025-3-29
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/actuator/shutdown").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .httpBasic();

        return http.build();
    }
}