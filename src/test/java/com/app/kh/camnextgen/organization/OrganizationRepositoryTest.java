package com.app.kh.camnextgen.organization;

import com.app.kh.camnextgen.organization.domain.Organization;
import com.app.kh.camnextgen.organization.domain.OrganizationType;
import com.app.kh.camnextgen.organization.repository.OrganizationRepository;
import com.app.kh.camnextgen.shared.config.AuditingConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(AuditingConfig.class)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrganizationRepositoryTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private Validator validator;

    @Test
    void shouldNormalizeCodeAndNameOnPersist() {
        Organization organization = newOrganization("  CamNextGen ", " cam-next ", OrganizationType.SYSTEM);

        organizationRepository.saveAndFlush(organization);

        assertThat(organization.getName()).isEqualTo("CamNextGen");
        assertThat(organization.getCode()).isEqualTo("CAM-NEXT");
    }

    @Test
    void shouldRejectDuplicateCode() {
        organizationRepository.saveAndFlush(newOrganization("Org A", "DUP", OrganizationType.SYSTEM));
        Organization duplicateCode = newOrganization("Org B", "dup", OrganizationType.CUSTOM);

        assertThatThrownBy(() -> organizationRepository.saveAndFlush(duplicateCode))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectDuplicateName() {
        organizationRepository.saveAndFlush(newOrganization("Same Name", "CODE1", OrganizationType.SYSTEM));
        Organization duplicateName = newOrganization("Same Name", "CODE2", OrganizationType.CUSTOM);

        assertThatThrownBy(() -> organizationRepository.saveAndFlush(duplicateName))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldFailValidationForBlankOrMalformedFields() {
        Organization invalid = newOrganization("   ", "bad space", OrganizationType.SYSTEM);

        Set<ConstraintViolation<Organization>> violations = validator.validate(invalid);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("name", "code");
    }

    private Organization newOrganization(String name, String code, OrganizationType type) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setCode(code);
        organization.setDescription("Test organization");
        organization.setType(type);
        return organization;
    }
}
