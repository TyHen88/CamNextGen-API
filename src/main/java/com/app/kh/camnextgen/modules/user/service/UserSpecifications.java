package com.app.kh.camnextgen.modules.user.service;

import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.domain.UserStatus;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> hasEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.like(cb.lower(root.get("email")), "%%" + email.toLowerCase() + "%%");
    }

    public static Specification<User> hasStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            UserStatus value = UserStatus.valueOf(status);
            return (root, query, cb) -> cb.equal(root.get("status"), value);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("INVALID_STATUS", "Invalid status value");
        }
    }
}
