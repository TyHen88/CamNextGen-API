package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.auth.domain.RefreshToken;
import com.app.kh.camnextgen.modules.user.domain.User;

public interface TokenService {
    RefreshToken createRefreshToken(User user);

    RefreshToken validateRefreshToken(String token);

    void revokeToken(RefreshToken token);
}
