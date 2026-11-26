package cyberpulse.soc.dto;

import java.util.List;
import java.util.UUID;

public record SocPostureResponse(
        long openIncidents,
        long slaBreachedCount,
        long slaAtRiskCount,
        List<SlaIncidentStatus> slaAlerts,
        List<MitreTechniqueSummary> mitreTechniques
) {
    public record SlaIncidentStatus(
            UUID incidentId,
            String incidentNumber,
            String title,
            String severity,
            String status,
            long elapsedMinutes,
            long slaTargetMinutes,
            boolean breached
    ) {
    }

    public record MitreTechniqueSummary(
            String tacticId,
            String tacticName,
            String techniqueId,
            String techniqueName,
            long detectionCount
    ) {
    }
}
