package cyberpulse.ai.service;

import cyberpulse.ai.dto.AiTriageResponse;
import cyberpulse.common.exception.ResourceNotFoundException;
import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.incident.entity.Incident;
import cyberpulse.incident.repository.IncidentNoteRepository;
import cyberpulse.incident.repository.IncidentRepository;
import cyberpulse.risk.entity.RiskAssessment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generates AI-assisted incident triage summaries and remediation playbooks.
 * Uses Gemini or OpenAI when an API key is configured, and falls back to a
 * deterministic SOC playbook when offline or unconfigured.
 */
@Service
@Slf4j
public class AiTriageService {

    private final IncidentRepository incidentRepository;
    private final IncidentNoteRepository incidentNoteRepository;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final String provider;
    private final String apiKey;
    private final String model;

    public AiTriageService(
            IncidentRepository incidentRepository,
            IncidentNoteRepository incidentNoteRepository,
            ObjectMapper objectMapper,
            @Value("${cyberpulse.ai.provider:gemini}") String provider,
            @Value("${cyberpulse.ai.api-key:}") String apiKey,
            @Value("${cyberpulse.ai.model:gemini-2.5-flash}") String model
    ) {
        this.incidentRepository = incidentRepository;
        this.incidentNoteRepository = incidentNoteRepository;
        this.objectMapper = objectMapper;
        this.provider = provider != null ? provider.trim().toLowerCase() : "gemini";
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = model != null ? model.trim() : "gemini-2.5-flash";

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(15_000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    @Transactional(readOnly = true)
    public AiTriageResponse triageIncident(UUID incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + incidentId));

        RiskAssessment risk = incident.getRiskAssessment();
        ThreatDetection detection = risk != null ? risk.getThreatDetection() : null;
        SecurityEvent event = detection != null ? detection.getEvent() : null;
        int noteCount = incidentNoteRepository.findByIncidentIdOrderByCreatedAtAsc(incidentId).size();

        if (!apiKey.isEmpty()) {
            try {
                String prompt = buildPrompt(incident, risk, detection, event, noteCount);
                if ("openai".equals(provider)) {
                    return callOpenAi(incident, prompt);
                }
                return callGemini(incident, prompt);
            } catch (Exception ex) {
                log.warn("External AI triage failed for incident={}, falling back to deterministic playbook: {}",
                        incident.getIncidentNumber(), ex.getMessage());
            }
        }

        return buildDeterministicPlaybook(incident, risk, detection, event);
    }

    private AiTriageResponse callGemini(Incident incident, String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        String raw = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        JsonNode root = objectMapper.readTree(raw);
        String text = root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText("");
        return parseStructuredAiJson(incident, "GEMINI (" + model + ")", text);
    }

    private AiTriageResponse callOpenAi(Incident incident, String prompt) {
        Map<String, Object> body = Map.of(
                "model", model,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a senior SOC Tier-3 analyst. Respond with valid JSON only."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        String raw = restClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        JsonNode root = objectMapper.readTree(raw);
        String text = root.path("choices").path(0).path("message").path("content").asText("");
        return parseStructuredAiJson(incident, "OPENAI (" + model + ")", text);
    }

    private AiTriageResponse parseStructuredAiJson(Incident incident, String providerLabel, String jsonText) {
        JsonNode parsed = objectMapper.readTree(jsonText);
        List<String> actions = new ArrayList<>();
        parsed.path("recommendedActions").forEach(node -> {
            if (!node.asText().isBlank()) {
                actions.add(node.asText());
            }
        });

        String summary = parsed.path("summary").asText("");
        String vector = parsed.path("likelyAttackVector").asText("");
        String priority = parsed.path("priorityAssessment").asText("");

        if (summary.isBlank() || actions.isEmpty()) {
            throw new IllegalStateException("Incomplete AI JSON payload");
        }

        return new AiTriageResponse(
                incident.getId(),
                incident.getIncidentNumber(),
                providerLabel,
                summary,
                vector.isBlank() ? "Under investigation" : vector,
                actions,
                priority.isBlank() ? incident.getSeverity().name() : priority,
                Instant.now()
        );
    }

    private String buildPrompt(
            Incident incident,
            RiskAssessment risk,
            ThreatDetection detection,
            SecurityEvent event,
            int noteCount
    ) {
        return """
                Analyze this security incident and return JSON with keys:
                summary (string), likelyAttackVector (string), recommendedActions (array of 4-6 concise action strings), priorityAssessment (string).
                Incident: number=%s, title=%s, severity=%s, status=%s, description=%s, existingNotes=%d
                RiskScore: %s, RiskSeverity: %s
                ThreatType: %s, Confidence: %s
                Event: type=%s, sourceIp=%s, username=%s, service=%s
                """.formatted(
                incident.getIncidentNumber(),
                incident.getTitle(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getDescription(),
                noteCount,
                risk != null ? risk.getRiskScore() : "N/A",
                risk != null ? risk.getSeverity() : "N/A",
                detection != null ? detection.getThreatType() : "N/A",
                detection != null ? detection.getConfidenceScore() : "N/A",
                event != null ? event.getEventType() : "N/A",
                event != null && event.getSourceIp() != null ? event.getSourceIp().getHostAddress() : "N/A",
                event != null ? event.getUsername() : "N/A",
                event != null ? event.getService() : "N/A"
        );
    }

    private AiTriageResponse buildDeterministicPlaybook(
            Incident incident,
            RiskAssessment risk,
            ThreatDetection detection,
            SecurityEvent event
    ) {
        String threatType = detection != null ? detection.getThreatType() : "GENERAL_SECURITY_INCIDENT";
        String sourceIp = event != null && event.getSourceIp() != null
                ? event.getSourceIp().getHostAddress()
                : "unknown source IP";
        String username = event != null && event.getUsername() != null
                ? event.getUsername()
                : "affected account";
        String service = event != null && event.getService() != null
                ? event.getService()
                : "target service";
        Integer score = risk != null ? risk.getRiskScore() : null;

        String vector = switch (threatType) {
            case "BRUTE_FORCE" -> "Automated credential stuffing / brute-force authentication attempts from " + sourceIp;
            case "PORT_SCAN" -> "Network reconnaissance and service enumeration originating from " + sourceIp;
            case "SUSPICIOUS_LOGIN" -> "Anomalous authentication / multi-IP session activity for user " + username;
            default -> "Suspicious security activity targeting " + service;
        };

        List<String> actions = switch (threatType) {
            case "BRUTE_FORCE" -> List.of(
                    "Temporarily block or rate-limit source IP " + sourceIp + " at the edge WAF/firewall.",
                    "Verify whether any successful logins occurred for " + username + " from " + sourceIp + ".",
                    "Enforce MFA and force password rotation if credential compromise is suspected.",
                    "Correlate related failed login events across other services in the last 24 hours."
            );
            case "PORT_SCAN" -> List.of(
                    "Block source IP " + sourceIp + " on perimeter firewall and inspect IDS/IPS logs.",
                    "Audit exposed ports and confirm unnecessary services on " + service + " are closed.",
                    "Check for follow-on exploitation or unauthorized access events from " + sourceIp + ".",
                    "Document findings and update threat-intel blocklists."
            );
            case "SUSPICIOUS_LOGIN" -> List.of(
                    "Revoke active refresh tokens and force re-authentication for " + username + ".",
                    "Verify login geo/IP history (" + sourceIp + ") with the account owner.",
                    "Review audit logs for privilege changes or sensitive actions after login.",
                    "Enable step-up MFA verification for anomalous locations."
            );
            default -> List.of(
                    "Isolate affected host or service (" + service + ") if active compromise is suspected.",
                    "Preserve security event logs and audit trail for forensic review.",
                    "Review risk factors and assign a Tier-2/Tier-3 analyst for containment.",
                    "Update incident status to INVESTIGATING and record findings in notes."
            );
        };

        String summary = String.format(
                "Incident %s (%s severity%s) involves %s targeting %s. Immediate containment and log correlation are recommended.",
                incident.getIncidentNumber(),
                incident.getSeverity(),
                score != null ? ", risk score " + score + "/100" : "",
                threatType.replace('_', ' ').toLowerCase(),
                service
        );

        String priority = switch (incident.getSeverity()) {
            case CRITICAL -> "P1 — Immediate response required (SLA < 15m)";
            case HIGH -> "P2 — Urgent triage required (SLA < 1h)";
            case MEDIUM -> "P3 — Standard investigation (SLA < 4h)";
            case LOW -> "P4 — Scheduled review";
        };

        return new AiTriageResponse(
                incident.getId(),
                incident.getIncidentNumber(),
                "DETERMINISTIC_PLAYBOOK",
                summary,
                vector,
                actions,
                priority,
                Instant.now()
        );
    }
}
