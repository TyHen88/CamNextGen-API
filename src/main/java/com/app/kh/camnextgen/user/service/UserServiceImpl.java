package com.app.kh.camnextgen.user.service;

import com.app.kh.camnextgen.shared.exception.BusinessException;
import com.app.kh.camnextgen.user.domain.User;
import com.app.kh.camnextgen.user.domain.UserRole;
import com.app.kh.camnextgen.user.domain.Role;
import com.app.kh.camnextgen.user.domain.RolePermission;
import com.app.kh.camnextgen.user.dto.MenuPermissionResponse;
import com.app.kh.camnextgen.user.dto.UserResponse;
import com.app.kh.camnextgen.user.dto.UserSummaryResponse;
import com.app.kh.camnextgen.user.repository.RoleRepository;
import com.app.kh.camnextgen.user.repository.UserRepository;
import com.app.kh.camnextgen.user.repository.UserRoleRepository;
import com.app.kh.camnextgen.user.repository.UserSpecifications;
import com.app.kh.camnextgen.shared.infra.audit.service.AuditLogService;
import com.app.kh.camnextgen.shared.exception.NotFoundException;
import com.app.kh.camnextgen.shared.response.PageResponse;
import java.time.Instant;
import java.util.List;
import java.util.Comparator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuditLogService auditLogService;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserRoleRepository userRoleRepository,
                           AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findByIdWithAuthorities(userId)
            .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found"));
        return UserMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserSummaryResponse> listUsers(Pageable pageable, String keyword) {
        Page<User> page = userRepository.findAll(UserSpecifications.keyword(keyword), pageable);
        List<UserSummaryResponse> items = page.getContent().stream()
            .map(UserMapper::toSummary)
            .toList();
        return new PageResponse<>(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    @Transactional
    public UserResponse promoteToInstructor(Long targetUserId, Long actorUserId) {
        User user = userRepository.findByIdWithAuthorities(targetUserId)
            .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found"));

        if (userRoleRepository.existsByUserIdAndRoleCode(targetUserId, "ADMIN")) {
            throw new BusinessException("INVALID_ROLE_CHANGE", "Cannot promote admin to instructor");
        }
        if (userRoleRepository.existsByUserIdAndRoleCode(targetUserId, "INSTRUCTOR")) {
            throw new BusinessException("ROLE_ALREADY_ASSIGNED", "User is already an instructor");
        }

        Role instructorRole = roleRepository.findByCode("INSTRUCTOR")
            .orElseThrow(() -> new NotFoundException("ROLE_NOT_FOUND", "Instructor role not found"));
        userRoleRepository.deleteByUserIdAndRoleCode(targetUserId, "STUDENT");

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(instructorRole);
        userRole.setAssignedAt(Instant.now());
        userRoleRepository.save(userRole);

        auditLogService.logEvent("USER_PROMOTED_INSTRUCTOR", actorUserId,
            "{\"targetUserId\":" + targetUserId + "}");

        User refreshed = userRepository.findByIdWithAuthorities(targetUserId)
            .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found"));
        return UserMapper.toUserResponse(refreshed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuPermissionResponse> listMyMenus(Long userId) {
        User user = userRepository.findByIdWithAuthorities(userId)
            .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found"));

        return user.getRoles().stream()
            .flatMap(ur -> ur.getRole().getPermissions().stream())
            .map(RolePermission::getPermission)
            .filter(permission -> permission.getCode().startsWith("MENU_"))
            .map(permission -> new MenuPermissionResponse(permission.getCode(), permission.getName()))
            .distinct()
            .sorted(Comparator.comparing(MenuPermissionResponse::code))
            .toList();
    }
}
