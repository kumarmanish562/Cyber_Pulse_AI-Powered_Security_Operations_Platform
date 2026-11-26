package cyberpulse.alerting;

import cyberpulse.incident.event.IncidentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class IncidentWebhookListener {

    private final AlertWebhookService alertWebhookService;

    @Async("detectionTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onIncidentCreated(IncidentCreatedEvent event) {
        if ("HIGH".equalsIgnoreCase(event.severity())
                || "CRITICAL".equalsIgnoreCase(event.severity())) {
            alertWebhookService.sendHighSeverityIncidentAlert(
                    event.incidentId(),
                    event.severity(),
                    event.riskScore()
            );
        }
    }
}
