package com.example.library_management_be.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtils {
    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    public String generateAccessToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public String generateRefreshToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("refresh", true) // Thêm claim để phân biệt với access token
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("refresh", true) // Thêm claim để phân biệt với access token
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public String generateTokenValidation(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("validation", true)
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 5* 60 * 1000)) // Hết hạn sau 5 phút
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Kiểm tra token KHÔNG chứa claim "refresh"
            Boolean isRefresh = claims.get("refresh", Boolean.class);
            return isRefresh == null || !isRefresh;

        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid access token: " + e.getMessage());
            return false;
        }
    }
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody();

            // Kiểm tra token có claim "refresh" == true
            Boolean isRefresh = claims.get("refresh", Boolean.class);
            return Boolean.TRUE.equals(isRefresh);
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid refresh token: " + e.getMessage());
            return false;
        }
    }
    public boolean validateTokenValidation(String token, String email) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Kiểm tra token có claim "validation" == true
            Boolean isValidation = claims.get("validation", Boolean.class);
            if (Boolean.TRUE.equals(isValidation)) {
                String tokenEmail = claims.getSubject();
                return tokenEmail != null && tokenEmail.equals(email);
            }
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid validation token: " + e.getMessage());
            return false;
        }
    }
}
