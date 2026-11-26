CREATE TABLE service_api_keys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    key_prefix VARCHAR(16) NOT NULL,
    key_hash VARCHAR(64) NOT NULL,
    scopes VARCHAR(255) NOT NULL DEFAULT 'EVENT_WRITE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    last_used_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_service_api_keys_name UNIQUE (name),
    CONSTRAINT uk_service_api_keys_hash UNIQUE (key_hash)
);

CREATE INDEX idx_service_api_keys_active ON service_api_keys (active);
