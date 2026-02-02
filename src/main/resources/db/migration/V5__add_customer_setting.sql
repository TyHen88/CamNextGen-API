-- customer_setting: one row per user, settings stored as JSONB
CREATE TABLE customer_setting (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    settings_json JSONB NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_customer_setting_json UNIQUE (user_id)
);

CREATE INDEX idx_customer_setting_user_id ON customer_setting(user_id);
CREATE INDEX idx_customer_setting_is_deleted ON customer_setting(is_deleted);
