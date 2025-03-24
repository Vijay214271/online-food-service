package com.foodapp.user_service.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.foodapp.user_service.dto.LoginRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtil {
    private String secretKey = "MySecretKey1234";

    // public JwtUtil() throws NoSuchAlgorithmException {
    //     KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
    //     SecretKey sk = keyGen.generateKey();
    //     secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
    // }

    public String generateToken(LoginRequest request) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(request.getUsername()) 
                .setIssuer("DCB")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getKey())
                .compact();
    }

    public Key getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String userName = extractUserName(jwt);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}
