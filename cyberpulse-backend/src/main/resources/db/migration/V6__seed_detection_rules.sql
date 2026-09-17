INSERT INTO detection_rules (
    name,
    rule_type,
    description,
    severity,
    threshold_value,
    time_window_seconds,
    enabled
)
VALUES (
    'BRUTE_FORCE_LOGIN',
    'BRUTE_FORCE',
    'Detect repeated failed login attempts from the same source IP within a configured time window.',
    'HIGH',
    10,
    120,
    TRUE
)
ON CONFLICT (name) DO NOTHING;