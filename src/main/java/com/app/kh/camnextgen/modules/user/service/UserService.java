package com.app.kh.camnextgen.modules.user.service;

import com.app.kh.camnextgen.modules.user.dto.UserResponse;
import com.app.kh.camnextgen.modules.user.dto.UserSummaryResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getProfile(Long userId);
    PageResponse<UserSummaryResponse> listUsers(Pageable pageable, String keyword);
}
