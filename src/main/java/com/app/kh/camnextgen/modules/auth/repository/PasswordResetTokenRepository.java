package com.app.kh.camnextgen.modules.auth.repository;

import com.app.kh.camnextgen.modules.auth.domain.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
