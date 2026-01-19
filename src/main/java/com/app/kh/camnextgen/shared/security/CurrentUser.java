package com.app.kh.camnextgen.shared.security;

import com.app.kh.camnextgen.shared.exception.UnauthorizedException;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static Long getUserId() {
        UserPrincipal principal = getPrincipal();
        return principal.getId();
    }

    public static Set<String> getRoles() {
        return getPrincipal().getRoleCodes();
    }

    public static Set<String> getPermissions() {
        return getPrincipal().getPermissionCodes();
    }

    private static UserPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UnauthorizedException("Unauthenticated request");
        }
        return (UserPrincipal) authentication.getPrincipal();
    }
}
