-- ============================================================
-- CyberPulse
-- V9 - Extend Incident Management
-- ============================================================

-- ============================================================
-- INCIDENT NUMBER SEQUENCE
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS incident_number_seq
    START WITH 1
    INCREMENT BY 1;


-- ============================================================
-- INCIDENTS
-- ============================================================

ALTER TABLE incidents
    ADD COLUMN IF NOT EXISTS risk_assessment_id UUID;

ALTER TABLE incidents
    ADD COLUMN IF NOT EXISTS closed_at TIMESTAMPTZ;


-- ============================================================
-- INCIDENT → RISK ASSESSMENT
-- ============================================================

ALTER TABLE incidents
    ADD CONSTRAINT fk_incident_risk_assessment
    FOREIGN KEY (risk_assessment_id)
    REFERENCES risk_assessments(id)
    ON DELETE RESTRICT;


-- ============================================================
-- ONE INCIDENT PER RISK ASSESSMENT
-- ============================================================

CREATE UNIQUE INDEX IF NOT EXISTS uk_incidents_risk_assessment
    ON incidents(risk_assessment_id)
    WHERE risk_assessment_id IS NOT NULL;


-- ============================================================
-- INCIDENT INDEXES
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_incidents_risk_assessment
    ON incidents(risk_assessment_id);


-- ============================================================
-- INCIDENT EVENTS
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_incident_events_incident
    ON incident_events(incident_id);

CREATE INDEX IF NOT EXISTS idx_incident_events_event
    ON incident_events(event_id);


-- ============================================================
-- INCIDENT NOTES
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_incident_notes_incident
    ON incident_notes(incident_id);

CREATE INDEX IF NOT EXISTS idx_incident_notes_created_at
    ON incident_notes(created_at);