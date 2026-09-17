CREATE UNIQUE INDEX uk_threat_detections_rule_event
    ON threat_detections (rule_id, event_id);