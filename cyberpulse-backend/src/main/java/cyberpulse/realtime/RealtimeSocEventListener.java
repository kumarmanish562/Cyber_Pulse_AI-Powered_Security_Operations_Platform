package cyberpulse.realtime;

import cyberpulse.detection.event.ThreatDetectedEvent;
import cyberpulse.incident.event.IncidentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RealtimeSocEventListener {

    private final SseEventPublisher sseEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onThreatDetected(ThreatDetectedEvent event) {
        sseEventPublisher.publish("detection", Map.of(
                "threatDetectionId", event.threatDetectionId(),
                "threatType", event.threatType(),
                "timestamp", Instant.now().toString()
        ));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onIncidentCreated(IncidentCreatedEvent event) {
        sseEventPublisher.publish("incident", Map.of(
                "incidentId", event.incidentId(),
                "severity", event.severity(),
                "riskScore", event.riskScore() != null ? event.riskScore() : 0,
                "timestamp", Instant.now().toString()
        ));
    }
}
