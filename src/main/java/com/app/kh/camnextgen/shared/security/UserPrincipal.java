package com.app.kh.camnextgen.shared.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final Set<String> roleCodes;
    private final Set<String> permissionCodes;

    public UserPrincipal(Long id, String email, String passwordHash, Set<String> roleCodes, Set<String> permissionCodes) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleCodes = roleCodes == null ? Collections.emptySet() : roleCodes;
        this.permissionCodes = permissionCodes == null ? Collections.emptySet() : permissionCodes;
    }

    public Long getId() {
        return id;
    }

    public Set<String> getRoleCodes() {
        return roleCodes;
    }

    public Set<String> getPermissionCodes() {
        return permissionCodes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = roleCodes.stream()
                .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
                .collect(Collectors.toSet());
        Set<GrantedAuthority> permissions = permissionCodes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        roles.addAll(permissions);
        return roles;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
