package cyberpulse.audit.event;

import cyberpulse.audit.service.AuditLogService;
import cyberpulse.incident.event.IncidentNoteAddedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncidentNoteAuditListener {

    private final AuditLogService auditLogService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            IncidentNoteAddedEvent event
    ) {

        try {

            Map<String, Object> details =
                    Map.of(
                            "noteId",
                            event.noteId()
                    );


            auditLogService.record(

                    AuditAction.INCIDENT_NOTE_ADDED,

                    "Incident",

                    event.incidentId(),

                    details
            );

        } catch (Exception exception) {

            log.error(
                    "Failed to audit incident note: {}",
                    event.noteId(),
                    exception
            );
        }
    }
}