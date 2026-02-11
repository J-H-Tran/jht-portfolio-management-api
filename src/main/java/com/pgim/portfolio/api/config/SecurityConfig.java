package com.pgim.portfolio.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")  // restrict delete to ADMIN
//                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")     // restrict admin endpoints to ADMIN
                        .requestMatchers("v1/api/portfolios").permitAll()     // unrestrict portfolios
                        .requestMatchers("v1/api/portfolios/**").permitAll()  // unrestrict portfolios endpoints
                        .requestMatchers("v1/api/trades").permitAll()         // unrestrict trades endpoints
                        .requestMatchers("v1/api/trades/**").permitAll()      // unrestrict trades endpoints
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())   // enable basic auth
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // stateless session management
                );
        return http.build();
    }
}