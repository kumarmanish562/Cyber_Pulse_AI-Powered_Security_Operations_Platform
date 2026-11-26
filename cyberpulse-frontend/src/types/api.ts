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
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserResponse;
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
