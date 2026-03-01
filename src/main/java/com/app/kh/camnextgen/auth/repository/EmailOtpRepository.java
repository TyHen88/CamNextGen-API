package com.app.kh.camnextgen.auth.repository;

import com.app.kh.camnextgen.auth.domain.EmailOtp;
import com.app.kh.camnextgen.auth.domain.OtpPurpose;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByEmailAndPurposeAndUsedAtIsNullOrderByCreatedAtDesc(String email, OtpPurpose purpose);

    @Modifying
    @Query("UPDATE EmailOtp o SET o.usedAt = :usedAt WHERE o.email = :email AND o.purpose = :purpose AND o.usedAt IS NULL")
    void invalidateActiveByEmailAndPurpose(@Param("email") String email,
                                           @Param("purpose") OtpPurpose purpose,
                                           @Param("usedAt") Instant usedAt);
}
