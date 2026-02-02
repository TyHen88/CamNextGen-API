package com.app.kh.camnextgen.modules.setting.domain;

import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.shared.util.BaseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer_setting", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_setting_json", columnNames = { "user_id" })
})
@Getter
@Setter
public class UserSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_setting_user"))
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings_json", nullable = false, columnDefinition = "jsonb")
    private JsonNode settingsJson;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

}
