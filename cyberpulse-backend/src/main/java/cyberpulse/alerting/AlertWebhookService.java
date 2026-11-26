package cyberpulse.alerting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Dispatches real-time incident alerts to external Slack / Teams / PagerDuty-compatible webhooks.
 */
@Service
@Slf4j
public class AlertWebhookService {

    private final RestClient restClient;
    private final String webhookUrl;

    public AlertWebhookService(
            @Value("${cyberpulse.alerting.webhook-url:}") String webhookUrl
    ) {
        this.webhookUrl = webhookUrl != null ? webhookUrl.trim() : "";
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3_000);
        factory.setReadTimeout(5_000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public boolean isEnabled() {
        return !webhookUrl.isEmpty();
    }

    public void sendHighSeverityIncidentAlert(UUID incidentId, String severity, Integer riskScore) {
        if (!isEnabled()) {
            return;
        }

        String text = String.format(
                "[CyberPulse SOC] %s Severity Incident Created (ID: %s, Risk Score: %s)",
                severity,
                incidentId,
                riskScore != null ? riskScore : "N/A"
        );

        Map<String, Object> payload = Map.of(
                "text", text,
                "attachments", List.of(Map.of(
                        "color", "CRITICAL".equalsIgnoreCase(severity) ? "#dc2626" : "#ea580c",
                        "title", "High-Priority Security Incident",
                        "text", text,
                        "ts", Instant.now().getEpochSecond()
                ))
        );

        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("Failed to deliver alerting webhook for incident={}: {}", incidentId, ex.getMessage());
        }
    }
}
