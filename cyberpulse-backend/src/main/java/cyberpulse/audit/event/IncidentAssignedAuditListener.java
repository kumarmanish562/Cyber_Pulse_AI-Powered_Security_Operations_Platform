package cyberpulse.audit.event;

import cyberpulse.audit.service.AuditLogService;
import cyberpulse.incident.event.IncidentAssignedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncidentAssignedAuditListener {

    private final AuditLogService auditLogService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            IncidentAssignedEvent event
    ) {

        try {

            Map<String, Object> details =
                    new HashMap<>();


            details.put(
                    "previousAssignee",
                    event.previousAssignee()
            );


            details.put(
                    "newAssignee",
                    event.newAssignee()
            );


            auditLogService.record(

                    AuditAction.INCIDENT_ASSIGNED,

                    "Incident",

                    event.incidentId(),

                    details
            );

        } catch (Exception exception) {

            log.error(
                    "Failed to audit incident assignment: {}",
                    event.incidentId(),
                    exception
            );
        }
    }
}