package com.foodapp.orderservice.config;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain
) throws ServletException, IOException {
    String authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7);
        System.out.println("Extracted Token: " + token); // Log the token

        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            System.out.println("Token valid for user: " + username); // Log the username

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            System.out.println("Invalid Token"); // Log invalid token
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }
    } else {
        System.out.println("Authorization header missing or invalid"); // Log missing header
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header missing");
        return;
    }
    chain.doFilter(request, response);
}
}
