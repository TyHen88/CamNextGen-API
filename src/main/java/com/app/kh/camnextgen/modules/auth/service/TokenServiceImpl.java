package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.auth.domain.RefreshToken;
import com.app.kh.camnextgen.modules.auth.repository.RefreshTokenRepository;
import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.shared.config.JwtProperties;
import com.app.kh.camnextgen.shared.security.JwtTokenProvider;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final JwtProperties jwtProperties;

    public TokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                            JwtTokenProvider tokenProvider,
                            JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String createAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("status", user.getStatus().name());
        return tokenProvider.generateAccessToken(user.getId(), user.getEmail(), claims);
    }

    @Override
    public RefreshToken createRefreshToken(User user) {
        String token = tokenProvider.generateRefreshToken(user.getId());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setExpiresAt(Instant.now().plus(jwtProperties.getRefreshTokenTtl()));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken rotateRefreshToken(RefreshToken existingToken) {
        existingToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(existingToken);
        return createRefreshToken(existingToken.getUser());
    }
}
