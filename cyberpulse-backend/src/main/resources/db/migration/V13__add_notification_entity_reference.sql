ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS entity_type VARCHAR(100);

ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS entity_id UUID;

CREATE INDEX IF NOT EXISTS idx_notifications_entity
    ON notifications(entity_type, entity_id);