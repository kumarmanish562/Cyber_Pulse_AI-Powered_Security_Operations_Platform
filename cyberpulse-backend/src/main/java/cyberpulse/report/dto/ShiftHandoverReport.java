package cyberpulse.report.dto;

import java.time.Instant;
import java.util.List;

public record ShiftHandoverReport(
        Instant generatedAt,
        int windowHours,
        long totalEvents24h,
        long totalDetections24h,
        long openIncidents,
        long slaBreachedCount,
        double slaCompliancePercent,
        long activeBlockedIps,
        List<String> topMitreTechniques,
        String markdownSummary
) {
}
