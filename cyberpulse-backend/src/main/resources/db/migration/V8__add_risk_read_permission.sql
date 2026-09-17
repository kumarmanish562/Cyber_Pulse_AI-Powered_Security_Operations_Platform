INSERT INTO permissions (
    id,
    name,
    description
)
VALUES (
    gen_random_uuid(),
    'RISK_READ',
    'View risk assessments and risk scores'
)
ON CONFLICT (name) DO NOTHING;


INSERT INTO role_permissions (
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.name = 'RISK_READ'
  AND r.name IN (
      'ADMIN',
      'SECURITY_ANALYST',
      'SECURITY_ENGINEER',
      'VIEWER'
  )
ON CONFLICT DO NOTHING;