package com.warehouse.warehousemanager.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    private SecretKey getSigningKey() {
        // Create a key from the secret string, ensuring it's the right size for HS512
        // If the secret string is too short, we'll use the Keys.secretKeyFor method
        byte[] keyBytes = jwtSecret.getBytes();
        SecretKey key;

        if (keyBytes.length < 64) { // 512 bits = 64 bytes
            // If the secret is too short, we extend it to be secure enough
            StringBuilder extendedSecret = new StringBuilder(jwtSecret);
            while (extendedSecret.length() < 64) {
                extendedSecret.append(jwtSecret);
            }
            key = Keys.hmacShaKeyFor(extendedSecret.toString().getBytes());
        } else {
            key = Keys.hmacShaKeyFor(keyBytes);
        }

        return key;
    }

    public String generateToken(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}