package com.app.kh.camnextgen.modules.user.service;

import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.dto.UserFilter;
import com.app.kh.camnextgen.modules.user.dto.UserResponse;
import com.app.kh.camnextgen.modules.user.dto.UserSummaryResponse;
import com.app.kh.camnextgen.modules.user.repo.UserRepository;
import com.app.kh.camnextgen.shared.api.PageResponse;
import com.app.kh.camnextgen.shared.exception.NotFoundException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserSummaryResponse> listUsers(UserFilter filter, Pageable pageable) {
        Specification<User> spec = Specification.where(UserSpecifications.hasEmail(filter.email()))
                .and(UserSpecifications.hasStatus(filter.status()));
        Page<User> page = userRepository.findAll(spec, pageable);
        return new PageResponse<>(
                page.map(this::toSummary).getContent(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }

    private UserResponse toResponse(User user) {
        Set<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getCode())
                .collect(Collectors.toSet());
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus().name(),
                roles
        );
    }

    private UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus().name()
        );
    }
}
