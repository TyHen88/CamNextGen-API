package com.app.kh.camnextgen.modules.user.service;

import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.dto.UserResponse;
import com.app.kh.camnextgen.modules.user.dto.UserSummaryResponse;
import com.app.kh.camnextgen.modules.user.repository.UserRepository;
import com.app.kh.camnextgen.modules.user.repository.UserSpecifications;
import com.app.kh.camnextgen.shared.exception.NotFoundException;
import com.app.kh.camnextgen.shared.response.PageResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
}
