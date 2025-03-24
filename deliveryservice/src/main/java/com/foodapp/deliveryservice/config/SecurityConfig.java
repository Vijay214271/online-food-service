package com.foodapp.deliveryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtUtil JwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.JwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disabling CSRF protection (can be enabled if needed)
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/api/delivery/assign", "/api/delivery/save").hasRole("ADMIN")  // Only ADMIN can assign or save deliveries
                .requestMatchers("/api/delivery/**").hasAnyRole("ADMIN","USER") // Admins can access all /api/delivery/** routes (get, update, delete)
                .requestMatchers("/api/delivery").hasRole("USER") // Users can view deliveries (GET all deliveries)
                .requestMatchers("/api/delivery/order/**").hasRole("USER") // Users can access deliveries by order ID
                .anyRequest().permitAll() // Allow other requests without authentication
            ).sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new JwtRequestFilter(JwtUtil), UsernamePasswordAuthenticationFilter.class)
            ; // Enable basic authentication with defaults
        
        return http.build();
    }
}
