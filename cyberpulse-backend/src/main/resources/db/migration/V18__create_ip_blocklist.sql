CREATE TABLE blocked_ips (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ip_address VARCHAR(45) NOT NULL,
    reason TEXT NOT NULL,
    source VARCHAR(30) NOT NULL DEFAULT 'MANUAL',
    severity VARCHAR(20) NOT NULL DEFAULT 'HIGH',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_blocked_ips_address UNIQUE (ip_address)
);

CREATE INDEX idx_blocked_ips_active ON blocked_ips (active);
