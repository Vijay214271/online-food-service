package com.example.API_GATEWAY.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.example.API_GATEWAY.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter){
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http
            .csrf(csrf->csrf.disable())
            .authorizeExchange(authorizeExchange->
            authorizeExchange
            .pathMatchers("/auth/**").permitAll()
            .pathMatchers(HttpMethod.GET,"/users/**").authenticated()
            .pathMatchers("/admin/**").hasAuthority("ADMIN")
            .anyExchange().authenticated()
            )
            // .addFilterBefore(jwtAuthFilter,)
            .build();
    }
}
