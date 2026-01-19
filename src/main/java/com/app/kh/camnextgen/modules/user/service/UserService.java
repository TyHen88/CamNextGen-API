package com.app.kh.camnextgen.modules.user.service;

import com.app.kh.camnextgen.modules.user.dto.UserFilter;
import com.app.kh.camnextgen.modules.user.dto.UserResponse;
import com.app.kh.camnextgen.modules.user.dto.UserSummaryResponse;
import com.app.kh.camnextgen.shared.api.PageResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getProfile(Long userId);

    PageResponse<UserSummaryResponse> listUsers(UserFilter filter, Pageable pageable);
}
