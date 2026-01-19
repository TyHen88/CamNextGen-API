package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.auth.domain.RefreshToken;
import com.app.kh.camnextgen.modules.auth.repo.RefreshTokenRepository;
import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.shared.config.JwtProperties;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public TokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(jwtProperties.getRefreshTokenTtl()));
        return refreshTokenRepository.save(token);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException("INVALID_TOKEN", "Invalid refresh token"));
        if (token.getRevokedAt() != null) {
            throw new BusinessException("TOKEN_REVOKED", "Refresh token revoked");
        }
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("TOKEN_EXPIRED", "Refresh token expired");
        }
        return token;
    }

    @Override
    @Transactional
    public void revokeToken(RefreshToken token) {
        token.setRevokedAt(Instant.now());
        refreshTokenRepository.save(token);
    }
}
