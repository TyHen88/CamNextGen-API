package com.app.kh.camnextgen.modules.user.repository;

import com.app.kh.camnextgen.modules.user.domain.User;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {
    private UserSpecifications() {}

    public static Specification<User> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("email")), like),
                cb.like(cb.lower(root.get("fullName")), like)
            );
        };
    }
}
