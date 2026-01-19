package com.app.kh.camnextgen.shared.security;

import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.repo.UserRepository;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getCode())
                .collect(Collectors.toSet());
        Set<String> permissions = user.getUserRoles().stream()
                .flatMap(userRole -> userRole.getRole().getRolePermissions().stream())
                .map(rolePermission -> rolePermission.getPermission().getCode())
                .collect(Collectors.toSet());
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash(), roles, permissions);
    }
}
