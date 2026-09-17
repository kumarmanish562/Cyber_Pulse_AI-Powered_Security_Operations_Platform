package cyberpulse.audit.event;

import cyberpulse.audit.service.AuditLogService;
import cyberpulse.incident.event.IncidentStatusChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncidentStatusAuditListener {

    private final AuditLogService auditLogService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            IncidentStatusChangedEvent event
    ) {

        try {

            Map<String, Object> details =
                    Map.of(

                            "oldStatus",
                            event.oldStatus().name(),

                            "newStatus",
                            event.newStatus().name()
                    );


            auditLogService.record(

                    AuditAction.INCIDENT_STATUS_CHANGED,

                    "Incident",

                    event.incidentId(),

                    details
            );

        } catch (Exception exception) {

            log.error(
                    "Failed to audit incident status change: {}",
                    event.incidentId(),
                    exception
            );
        }
    }
}