ALTER TABLE incident_events
    ADD COLUMN id UUID;

UPDATE incident_events
SET id = gen_random_uuid()
WHERE id IS NULL;

ALTER TABLE incident_events
    ALTER COLUMN id SET NOT NULL;

ALTER TABLE incident_events
    ADD CONSTRAINT uk_incident_events_id UNIQUE (id);