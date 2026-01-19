package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.auth.domain.RefreshToken;
import com.app.kh.camnextgen.modules.user.domain.User;

public interface TokenService {
    String createAccessToken(User user);
    RefreshToken createRefreshToken(User user);
    RefreshToken rotateRefreshToken(RefreshToken existingToken);
}
