-- ============================================================
-- CyberPulse
-- V7 - Enhance Risk Assessments
-- ============================================================


-- ============================================================
-- RENAME COLUMN
-- ============================================================

ALTER TABLE risk_assessments
    RENAME COLUMN detection_id
    TO threat_detection_id;


-- ============================================================
-- ADD RISK FACTOR COLUMNS
-- ============================================================

ALTER TABLE risk_assessments
    ADD COLUMN base_score INTEGER NOT NULL DEFAULT 0;

ALTER TABLE risk_assessments
    ADD COLUMN confidence_score INTEGER NOT NULL DEFAULT 0;

ALTER TABLE risk_assessments
    ADD COLUMN frequency_score INTEGER NOT NULL DEFAULT 0;

ALTER TABLE risk_assessments
    ADD COLUMN severity_score INTEGER NOT NULL DEFAULT 0;


-- ============================================================
-- RENAME CREATED_AT
-- ============================================================

ALTER TABLE risk_assessments
    RENAME COLUMN created_at
    TO assessed_at;


-- ============================================================
-- UNIQUE CONSTRAINT
-- One risk assessment per threat detection
-- ============================================================

ALTER TABLE risk_assessments
    ADD CONSTRAINT uk_risk_assessment_detection
    UNIQUE (threat_detection_id);


-- ============================================================
-- SCORE VALIDATION
-- ============================================================

ALTER TABLE risk_assessments
    ADD CONSTRAINT chk_base_score
    CHECK (
        base_score BETWEEN 0 AND 100
    );

ALTER TABLE risk_assessments
    ADD CONSTRAINT chk_confidence_score
    CHECK (
        confidence_score BETWEEN 0 AND 100
    );

ALTER TABLE risk_assessments
    ADD CONSTRAINT chk_frequency_score
    CHECK (
        frequency_score BETWEEN 0 AND 100
    );

ALTER TABLE risk_assessments
    ADD CONSTRAINT chk_severity_score
    CHECK (
        severity_score BETWEEN 0 AND 100
    );


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_risk_assessments_assessed_at
    ON risk_assessments (assessed_at);

CREATE INDEX idx_risk_assessments_score
    ON risk_assessments (risk_score);