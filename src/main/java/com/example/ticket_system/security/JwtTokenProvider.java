package com.example.ticket_system.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

// Utility class for JWT token creation and validation
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String secretKeyBase64;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyBase64.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userId, String role) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plus(1, ChronoUnit.HOURS));

        JwtBuilder builder = Jwts.builder()
                .issuedAt(issuedAt)
                .expiration(expiration)
                .subject(userId)
                .claim("role", role)
                .signWith(secretKey, SignatureAlgorithm.HS256);

        return builder.compact();
    }

    public Claims validateTokenAndGetClaims(String jwtToken) {
        JwtParserBuilder parserBuilder = Jwts.parser()
                .verifyWith(secretKey);

        return parserBuilder
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }
}
