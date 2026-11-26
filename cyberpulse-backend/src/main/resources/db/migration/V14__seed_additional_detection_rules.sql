INSERT INTO detection_rules (
    name,
    rule_type,
    description,
    severity,
    threshold_value,
    time_window_seconds,
    enabled
)
VALUES
    (
        'PORT_SCAN_DETECTION',
        'PORT_SCAN',
        'Detect repeated port scanning activity from the same source IP within a configured time window.',
        'HIGH',
        5,
        180,
        TRUE
    ),
    (
        'SUSPICIOUS_LOGIN_ACTIVITY',
        'SUSPICIOUS_LOGIN',
        'Detect explicit suspicious logins or logins for the same user from multiple distinct source IPs.',
        'MEDIUM',
        2,
        600,
        TRUE
    )
ON CONFLICT (name) DO NOTHING;
