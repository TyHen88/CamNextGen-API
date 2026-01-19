package com.app.kh.camnextgen.shared.security;

import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.domain.UserRole;
import com.app.kh.camnextgen.modules.user.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toUserDetails(user);
    }

    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findByIdWithAuthorities(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toUserDetails(user);
    }

    private UserDetails toUserDetails(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (UserRole userRole : user.getRoles()) {
            String roleCode = userRole.getRole().getCode();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode));
            userRole.getRole().getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getPermission().getCode()))
            );
        }
        boolean enabled = user.getStatus().name().equals("ACTIVE");
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(), authorities, enabled);
    }
}
