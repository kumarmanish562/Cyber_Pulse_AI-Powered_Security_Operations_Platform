-- ============================================================
-- CyberPulse
-- V11 - Add Audit Log Composite Indexes
-- PostgreSQL
-- ============================================================

-- Supports queries filtering by user and sorting
-- newest audit logs first.
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_created_at
    ON audit_logs (user_id, created_at DESC);


-- Supports queries filtering by action and sorting
-- newest audit logs first.
CREATE INDEX IF NOT EXISTS idx_audit_logs_action_created_at
    ON audit_logs (action, created_at DESC);