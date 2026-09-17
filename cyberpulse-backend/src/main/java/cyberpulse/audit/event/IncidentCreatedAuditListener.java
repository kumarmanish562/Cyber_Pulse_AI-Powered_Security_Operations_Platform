package cyberpulse.audit.event;

import cyberpulse.audit.service.AuditLogService;
import cyberpulse.incident.event.IncidentCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncidentCreatedAuditListener {

    private final AuditLogService auditLogService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            IncidentCreatedEvent event
    ) {

        try {

            Map<String, Object> details =
                    Map.of(

                            "riskAssessmentId",
                            event.riskAssessmentId(),

                            "riskScore",
                            event.riskScore(),

                            "severity",
                            event.severity()
                    );


            auditLogService.record(

                    AuditAction.INCIDENT_CREATED,

                    "Incident",

                    event.incidentId(),

                    details
            );

        } catch (Exception exception) {

            log.error(
                    "Failed to create audit log for incident {}",
                    event.incidentId(),
                    exception
            );
        }
    }
}