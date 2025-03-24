package com.foodapp.orderservice.config;

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
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                 // Authentication required for all /order/** endpoints
                .requestMatchers("/order").hasRole("USER") // Users can create, place orders
                .requestMatchers("/order/all").hasAuthority("ADMIN")
                .requestMatchers("/order/**").hasAuthority("ADMIN") // Admins can access other order routes like update, delete, and get all orders
                .anyRequest().authenticated()) // Allow other requests (non-order-related) without authentication
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new JwtRequestFilter(JwtUtil), UsernamePasswordAuthenticationFilter.class)
                ; 
        
        return http.build();
    }

}
