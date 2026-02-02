package com.app.kh.camnextgen.modules.setting.repository;

import com.app.kh.camnextgen.modules.setting.domain.UserSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

    Optional<UserSetting> findByUserIdAndDeletedFalse(Long userId);

    Optional<UserSetting> findByUserId(Long userId);
}
