-- ============================================================
-- CyberPulse
-- V4 - RBAC Permissions
-- ============================================================

-- ============================================================
-- PERMISSIONS
-- ============================================================

INSERT INTO permissions (name, description)
VALUES

    ('USER_READ',
     'View user information'),

    ('USER_WRITE',
     'Create or update user information'),

    ('ROLE_ASSIGN',
     'Assign roles to users'),

    ('ROLE_REMOVE',
     'Remove roles from users'),

    ('EVENT_READ',
     'View security events'),

    ('EVENT_WRITE',
     'Create security events'),

    ('EVENT_DELETE',
     'Delete security events'),

    ('INCIDENT_READ',
     'View security incidents'),

    ('INCIDENT_UPDATE',
     'Update security incidents'),

    ('INCIDENT_INVESTIGATE',
     'Investigate security incidents'),

    ('RULE_READ',
     'View detection rules'),

    ('RULE_WRITE',
     'Create or update detection rules'),

    ('RULE_DELETE',
     'Delete detection rules'),

    ('AUDIT_READ',
     'View audit logs'),

    ('NOTIFICATION_READ',
     'View notifications')

ON CONFLICT (name) DO NOTHING;


-- ============================================================
-- ADMIN
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)

SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN'

ON CONFLICT (role_id, permission_id) DO NOTHING;


-- ============================================================
-- SECURITY ANALYST
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)

SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.name IN (
        'EVENT_READ',
        'INCIDENT_READ',
        'INCIDENT_UPDATE',
        'INCIDENT_INVESTIGATE'
    )
WHERE r.name = 'SECURITY_ANALYST'

ON CONFLICT (role_id, permission_id) DO NOTHING;


-- ============================================================
-- SECURITY ENGINEER
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)

SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.name IN (
        'EVENT_READ',
        'EVENT_WRITE',
        'RULE_READ',
        'RULE_WRITE',
        'RULE_DELETE'
    )
WHERE r.name = 'SECURITY_ENGINEER'

ON CONFLICT (role_id, permission_id) DO NOTHING;


-- ============================================================
-- VIEWER
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)

SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.name IN (
        'EVENT_READ',
        'INCIDENT_READ'
    )
WHERE r.name = 'VIEWER'

ON CONFLICT (role_id, permission_id) DO NOTHING;