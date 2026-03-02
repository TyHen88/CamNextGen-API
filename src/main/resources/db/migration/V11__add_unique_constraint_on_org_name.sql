-- V11: Ensure organization names are unique to align with repository semantics.

ALTER TABLE organizations
    ADD CONSTRAINT uk_organizations_name UNIQUE (name);

CREATE INDEX IF NOT EXISTS idx_organizations_name ON organizations(name);
