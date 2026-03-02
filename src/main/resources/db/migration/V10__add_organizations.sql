-- V10: Add organizations table and link users to it.
-- The default organization 'CamNextGen' is seeded automatically.

-- 1. Create organizations table
CREATE TABLE organizations (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    type        VARCHAR(20) NOT NULL DEFAULT 'SYSTEM',
    code        VARCHAR(60)  NOT NULL,
    description TEXT,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by  BIGINT,
    updated_by  BIGINT,
    version     BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uk_organizations_code UNIQUE (code)
);

CREATE INDEX idx_organizations_code      ON organizations(code);
CREATE INDEX idx_organizations_is_active ON organizations(is_active);

-- 2. Add organization_id FK column to users (nullable so existing rows don't break)
ALTER TABLE users ADD COLUMN organization_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT fk_users_organization
    FOREIGN KEY (organization_id) REFERENCES organizations(id);

CREATE INDEX idx_users_organization_id ON users(organization_id);

-- 3. Seed the default organization
INSERT INTO organizations (name, code, description, is_active, type)
VALUES ('CamNextGen', 'CAMNEXTGEN', 'Default platform organization', TRUE, 'SYSTEM');

-- 4. Backfill existing users to belong to the default organization
UPDATE users
SET organization_id = (SELECT id FROM organizations WHERE code = 'CAMNEXTGEN')
WHERE organization_id IS NULL;

-- 5. Make organization_id NOT NULL now that all rows have a value
ALTER TABLE users ALTER COLUMN organization_id SET NOT NULL;
