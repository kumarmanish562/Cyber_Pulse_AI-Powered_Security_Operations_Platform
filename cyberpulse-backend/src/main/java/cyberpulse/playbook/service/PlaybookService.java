package cyberpulse.playbook.service;

import cyberpulse.alerting.AlertWebhookService;
import cyberpulse.blocklist.service.BlocklistService;
import cyberpulse.common.exception.ResourceNotFoundException;
import cyberpulse.incident.entity.Incident;
import cyberpulse.incident.entity.IncidentStatus;
import cyberpulse.incident.repository.IncidentRepository;
import cyberpulse.incident.service.IncidentService;
import cyberpulse.playbook.dto.ExecutePlaybookRequest.PlaybookAction;
import cyberpulse.playbook.dto.ExecutePlaybookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaybookService {

    private final IncidentRepository incidentRepository;
    private final IncidentService incidentService;
    private final BlocklistService blocklistService;
    private final AlertWebhookService alertWebhookService;

    @Transactional
    public ExecutePlaybookResponse execute(UUID incidentId, PlaybookAction action, String actorUsername) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + incidentId));

        List<String> steps = new ArrayList<>();

        switch (action) {
            case CONTAIN_AND_BLOCK_IP -> {
                if (incident.getStatus() == IncidentStatus.OPEN) {
                    incidentService.updateStatus(incidentId, IncidentStatus.INVESTIGATING);
                    steps.add("Transitioned incident status OPEN -> INVESTIGATING");
                }

                String sourceIp = extractSourceIp(incident);
                if (sourceIp != null) {
                    blocklistService.blockIpAutomatically(
                            sourceIp,
                            "SOAR Playbook containment from " + incident.getIncidentNumber(),
                            incident.getSeverity().name(),
                            Instant.now().plus(24, ChronoUnit.HOURS)
                    );
                    steps.add("Blocked source IP " + sourceIp + " for 24h");
                } else {
                    steps.add("No source IP attached to incident; skipped IP block");
                }

                incidentService.addNote(
                        incidentId,
                        "[SOAR Playbook: CONTAIN_AND_BLOCK_IP] Executed by " + actorUsername,
                        actorUsername
                );
                steps.add("Recorded containment note on incident");
            }
            case ESCALATE_ALERT -> {
                alertWebhookService.sendHighSeverityIncidentAlert(
                        incident.getId(),
                        incident.getSeverity().name(),
                        incident.getRiskAssessment() != null ? incident.getRiskAssessment().getRiskScore() : null
                );
                steps.add("Dispatched escalation webhook alert");

                incidentService.addNote(
                        incidentId,
                        "[SOAR Playbook: ESCALATE_ALERT] Escalated to Tier-3 responders by " + actorUsername,
                        actorUsername
                );
                steps.add("Recorded escalation note on incident");
            }
            case CLOSE_FALSE_POSITIVE -> {
                if (incident.getStatus() == IncidentStatus.OPEN) {
                    incidentService.updateStatus(incidentId, IncidentStatus.INVESTIGATING);
                }
                if (incident.getStatus() != IncidentStatus.RESOLVED && incident.getStatus() != IncidentStatus.CLOSED) {
                    incidentService.updateStatus(incidentId, IncidentStatus.RESOLVED);
                }
                incidentService.updateStatus(incidentId, IncidentStatus.CLOSED);
                steps.add("Closed incident as false positive");

                incidentService.addNote(
                        incidentId,
                        "[SOAR Playbook: CLOSE_FALSE_POSITIVE] Closed by " + actorUsername,
                        actorUsername
                );
                steps.add("Recorded false-positive closure note");
            }
        }

        return new ExecutePlaybookResponse(
                incident.getId(),
                incident.getIncidentNumber(),
                action.name(),
                steps,
                Instant.now()
        );
    }

    private String extractSourceIp(Incident incident) {
        var risk = incident.getRiskAssessment();
        var detection = risk != null ? risk.getThreatDetection() : null;
        var event = detection != null ? detection.getEvent() : null;
        return event != null && event.getSourceIp() != null ? event.getSourceIp().getHostAddress() : null;
    }
}
