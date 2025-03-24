package com.example.API_GATEWAY.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private static final String SECRET = "34beadec159437ad8edb5564c7bb8e2b87ac7171303c89441ea874c7903d91eec99b6d3742cfc3fcbf8fabc1d1ecfba5b913018a3d44bf0070c3fa9a2f5097d6aeca00079e4271c1f3736ed6c11303fd3be37409a76b67031ef1073a428785072e06292bbb93a8f8b11548b3a2eceb9fd272b176e92589ac1f14e23fc6b7f4a66bc662ae0ba2f81815849d14bbb473713d193acd48e95308933bc8def61df42853605be527c20967a27c8ffc8b295154f9768f975e56fc75833d78989ac1143deb039a9f0d752a8b7a2284fefb32878fdaed2164116920615bf3b12334a0c94f205333ca9723a69bc1a3290ad511e33993c16a761075f2540e9c296cedf94617";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    private static final int EXPIRATION_TIME = 86400000;

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // public Claims extractClaims(String token) {
    //     return Jwts.parser()
    //             .setSigningKey(key)
    //             .parseClaimsJws(token)
    //             .getBody();
    // }
    public Claims extractClaims(String token){
        return Jwts.parser().verifyWith((javax.crypto.SecretKey)key).build().parseSignedClaims(token).getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String getRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
}
