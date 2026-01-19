package com.app.kh.camnextgen.shared.security;

import com.app.kh.camnextgen.shared.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private final JwtProperties properties;
    private final Key signingKey;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long userId, String email, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getAccessTokenTtl());
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer(properties.getIssuer())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .addClaims(claims)
            .claim("email", email)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getRefreshTokenTtl());
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer(properties.getIssuer())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
