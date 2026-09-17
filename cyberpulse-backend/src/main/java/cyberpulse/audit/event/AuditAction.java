package cyberpulse.audit.event;

public enum AuditAction {

    // Authentication
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    LOGOUT,
    REFRESH_TOKEN,

    // User management
    USER_CREATED,
    ROLE_ASSIGNED,
    ROLE_REMOVED,

    // Security events
    SECURITY_EVENT_CREATED,
    SECURITY_EVENT_DELETED,

    // Detection rules
    RULE_CREATED,
    RULE_UPDATED,
    RULE_DISABLED,

    // Incidents
    INCIDENT_CREATED,
    INCIDENT_ASSIGNED,
    INCIDENT_STATUS_CHANGED,
    INCIDENT_NOTE_ADDED,

    // Account security
    PASSWORD_CHANGED,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED
}