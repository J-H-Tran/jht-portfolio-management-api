package com.pgim.portfolio.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    public SecurityConfig() {
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
//              .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")  // restrict delete to ADMIN
//              .requestMatchers("/admin/**").hasAnyRole("ADMIN")     // restrict admin endpoints to ADMIN
                .requestMatchers("v1/api/portfolios", "v1/api/trades").permitAll()     // unrestrict portfolios
                .requestMatchers("v1/api/portfolios/**", "v1/api/trades/**").permitAll()  // unrestrict portfolios endpoints
                .requestMatchers("/csrf", "/login").permitAll()  // unrestrict portfolios endpoints
                .anyRequest().authenticated()
            )
            .formLogin(AbstractHttpConfigurer::disable) // Use the new formLogin approach
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // stateless session management
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}