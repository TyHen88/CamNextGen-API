package com.app.kh.camnextgen.user.service;

import com.app.kh.camnextgen.user.dto.MenuPermissionResponse;
import com.app.kh.camnextgen.user.dto.UserResponse;
import com.app.kh.camnextgen.user.dto.UserSummaryResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getProfile(Long userId);
    PageResponse<UserSummaryResponse> listUsers(Pageable pageable, String keyword);
    UserResponse promoteToInstructor(Long targetUserId, Long actorUserId);
    List<MenuPermissionResponse> listMyMenus(Long userId);
}
