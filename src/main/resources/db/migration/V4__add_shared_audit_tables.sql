-- Create shared audit logs table
CREATE TABLE shared_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    event VARCHAR(120) NOT NULL,
    actor_user_id BIGINT NOT NULL,
    details_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create shared activity logs table
CREATE TABLE shared_activity_logs (
    id BIGSERIAL PRIMARY KEY,
    event VARCHAR(120) NOT NULL,
    actor_user_id BIGINT,
    details_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_shared_audit_logs_event ON shared_audit_logs(event);
CREATE INDEX idx_shared_audit_logs_actor_user_id ON shared_audit_logs(actor_user_id);
CREATE INDEX idx_shared_audit_logs_created_at ON shared_audit_logs(created_at);

CREATE INDEX idx_shared_activity_logs_event ON shared_activity_logs(event);
CREATE INDEX idx_shared_activity_logs_actor_user_id ON shared_activity_logs(actor_user_id);
CREATE INDEX idx_shared_activity_logs_created_at ON shared_activity_logs(created_at);
