package com.app.kh.camnextgen.auth.service;

import com.app.kh.camnextgen.auth.domain.RefreshToken;
import com.app.kh.camnextgen.user.domain.User;

public interface TokenService {
    String createAccessToken(User user);
    RefreshToken createRefreshToken(User user);
    RefreshToken rotateRefreshToken(RefreshToken existingToken);
}
