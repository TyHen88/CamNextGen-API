package com.app.kh.camnextgen.organization.domain;

import com.app.kh.camnextgen.shared.infra.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a school, company, or platform entity that users belong to.
 * The default organization is seeded as "CamNextGen" (code = CAMNEXTGEN).
 */
@Entity
@Table(
        name = "organizations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_organizations_code", columnNames = "code"),
                @UniqueConstraint(name = "uk_organizations_name", columnNames = "name")
        }
)
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-readable display name, e.g. "CamNextGen" or "Harvard University". */
    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200, unique = true)
    private String name;

    /** Short, uppercased, URL-safe code used as a stable identifier, e.g. "CAMNEXTGEN". */
    @NotBlank
    @Size(max = 60)
    @Pattern(regexp = "^[A-Z0-9_-]+$")
    @Column(nullable = false, unique = true, length = 60)
    private String code;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    @NotNull
    private OrganizationType type = OrganizationType.SYSTEM;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim().toUpperCase(Locale.ROOT);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
