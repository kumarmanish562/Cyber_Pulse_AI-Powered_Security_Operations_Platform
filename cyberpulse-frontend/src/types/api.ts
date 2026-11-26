export type Severity = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";

export type EventType =
  | "LOGIN_FAILED"
  | "LOGIN_SUCCESS"
  | "PORT_SCAN"
  | "MALWARE_DETECTED"
  | "SUSPICIOUS_LOGIN"
  | "BRUTE_FORCE"
  | "UNAUTHORIZED_ACCESS"
  | "FILE_MODIFIED"
  | "PRIVILEGE_ESCALATION";

export type DetectionRuleType = "BRUTE_FORCE" | "SUSPICIOUS_LOGIN" | "PORT_SCAN";

export type IncidentStatus = "OPEN" | "INVESTIGATING" | "RESOLVED" | "CLOSED";

export type IncidentSeverity = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";

export interface UserResponse {
  id: string;
  username: string;
  email: string;
  roles: string[];
  mfaEnabled?: boolean;
}

export interface AuthResponse {
  accessToken: string | null;
  refreshToken: string | null;
  tokenType: string;
  expiresIn: number;
  user: UserResponse;
  mfaRequired?: boolean;
}

export interface ServiceApiKey {
  id: string;
  name: string;
  keyPrefix: string;
  scopes: string;
  active: boolean;
  lastUsedAt: string | null;
  expiresAt: string | null;
  createdAt: string;
}

export interface CreateApiKeyResponse {
  apiKey: ServiceApiKey;
  rawKey: string;
}

export interface MfaSetupResponse {
  secret: string;
  otpauthUri: string;
}

export interface BlockedIp {
  id: string;
  ipAddress: string;
  reason: string;
  source: string;
  severity: Severity;
  active: boolean;
  expiresAt: string | null;
  createdAt: string;
}

export interface GlobalSearchResponse {
  query: string;
  incidents: Incident[];
  events: SecurityEvent[];
  blockedIps: BlockedIp[];
}

export interface IpThreatIntelResponse {
  ipAddress: string;
  totalEvents: number;
  failedLogins: number;
  portScans: number;
  currentlyBlocked: boolean;
  blockReason: string | null;
  externalAbuseConfidenceScore: number | null;
  externalProvider: string;
  riskVerdict: "CLEAN" | "SUSPICIOUS" | "MALICIOUS";
  checkedAt: string;
}

export interface SocPostureResponse {
  openIncidents: number;
  slaBreachedCount: number;
  slaAtRiskCount: number;
  slaAlerts: Array<{
    incidentId: string;
    incidentNumber: string;
    title: string;
    severity: Severity;
    status: IncidentStatus;
    elapsedMinutes: number;
    slaTargetMinutes: number;
    breached: boolean;
  }>;
  mitreTechniques: Array<{
    tacticId: string;
    tacticName: string;
    techniqueId: string;
    techniqueName: string;
    detectionCount: number;
  }>;
}

export type AttackScenario =
  | "SSH_BRUTE_FORCE"
  | "PORT_SCAN_SWEEP"
  | "IMPOSSIBLE_TRAVEL";

export interface SimulationResponse {
  scenario: AttackScenario;
  eventsInjected: number;
  attackerIp: string;
  summary: string;
  triggeredAt: string;
}

export interface ShiftHandoverReport {
  generatedAt: string;
  windowHours: number;
  totalEvents24h: number;
  totalDetections24h: number;
  openIncidents: number;
  slaBreachedCount: number;
  slaCompliancePercent: number;
  activeBlockedIps: number;
  topMitreTechniques: string[];
  markdownSummary: string;
}

export type PlaybookAction =
  | "CONTAIN_AND_BLOCK_IP"
  | "ESCALATE_ALERT"
  | "CLOSE_FALSE_POSITIVE";

export interface ExecutePlaybookResponse {
  incidentId: string;
  incidentNumber: string;
  action: PlaybookAction;
  stepsExecuted: string[];
  executedAt: string;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  code: string;
  message: string;
  path: string;
  correlationId?: string;
  errors?: Array<{ field: string; message: string }>;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface SpringPage<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface SecurityEvent {
  id: string;
  eventType: EventType;
  sourceIp: string | null;
  username: string | null;
  service: string | null;
  message: string | null;
  severity: Severity;
  eventTime: string;
  createdAt: string;
}

export interface CreateSecurityEventPayload {
  eventType: EventType;
  sourceIp?: string;
  username?: string;
  service?: string;
  message?: string;
  severity: Severity;
  eventTime: string;
}

export interface DetectionRule {
  id: string;
  name: string;
  ruleType: DetectionRuleType;
  description: string | null;
  severity: Severity;
  thresholdValue: number;
  timeWindowSeconds: number;
  enabled: boolean;
  createdBy: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateDetectionRulePayload {
  name: string;
  ruleType: DetectionRuleType;
  description?: string;
  severity: Severity;
  thresholdValue: number;
  timeWindowSeconds: number;
  enabled: boolean;
}

export interface ThreatDetection {
  id: string;
  ruleId: string;
  ruleName: string;
  eventId: string;
  threatType: string;
  confidenceScore: number;
  details: Record<string, unknown>;
  createdAt: string;
}

export interface RiskAssessment {
  id: string;
  threatDetectionId: string;
  eventId: string;
  ruleId: string;
  ruleName: string;
  riskScore: number;
  severity: Severity;
  baseScore: number;
  confidenceScore: number;
  frequencyScore: number;
  severityScore: number;
  factors: Record<string, unknown>;
  assessedAt: string;
}

export interface Incident {
  id: string;
  incidentNumber: string;
  title: string;
  description: string | null;
  status: IncidentStatus;
  severity: IncidentSeverity;
  riskAssessmentId: string | null;
  assignedTo: string | null;
  createdAt: string;
  updatedAt: string;
  resolvedAt: string | null;
  closedAt: string | null;
}

export interface IncidentNote {
  id: string;
  incidentId: string;
  authorId: string;
  content: string;
  createdAt: string;
}

export interface AuditLog {
  id: string;
  userId: string | null;
  action: string;
  entityType: string | null;
  entityId: string | null;
  ipAddress: string | null;
  userAgent: string | null;
  details: string | null;
  createdAt: string;
}

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  type: string;
  entityType: string | null;
  entityId: string | null;
  read: boolean;
  createdAt: string;
}

export interface AiTriageResponse {
  incidentId: string;
  incidentNumber: string;
  provider: string;
  summary: string;
  likelyAttackVector: string;
  recommendedActions: string[];
  priorityAssessment: string;
  generatedAt: string;
}
