package cyberpulse.blocklist.event;

import cyberpulse.blocklist.service.BlocklistService;
import cyberpulse.incident.event.IncidentCreatedEvent;
import cyberpulse.incident.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Automatically adds offending source IPs to the active blocklist for 24 hours
 * when HIGH or CRITICAL incidents are created from security events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AutoBlockThreatListener {

    private final IncidentRepository incidentRepository;
    private final BlocklistService blocklistService;

    @Async("detectionTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onIncidentCreated(IncidentCreatedEvent event) {
        if (!"HIGH".equalsIgnoreCase(event.severity())
                && !"CRITICAL".equalsIgnoreCase(event.severity())) {
            return;
        }

        incidentRepository.findById(event.incidentId()).ifPresent(incident -> {
            var risk = incident.getRiskAssessment();
            var detection = risk != null ? risk.getThreatDetection() : null;
            var secEvent = detection != null ? detection.getEvent() : null;
            if (secEvent != null && secEvent.getSourceIp() != null) {
                String ip = secEvent.getSourceIp().getHostAddress();
                blocklistService.blockIpAutomatically(
                        ip,
                        "Auto-blocked from incident " + incident.getIncidentNumber(),
                        event.severity(),
                        Instant.now().plus(24, ChronoUnit.HOURS)
                );
                log.info("Auto-blocked IP {} from incident {}", ip, incident.getIncidentNumber());
            }
        });
    }
}
