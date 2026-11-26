package cyberpulse.intel.dto;

import java.time.Instant;

public record IpThreatIntelResponse(
        String ipAddress,
        long totalEvents,
        long failedLogins,
        long portScans,
        boolean currentlyBlocked,
        String blockReason,
        Integer externalAbuseConfidenceScore,
        String externalProvider,
        String riskVerdict,
        Instant checkedAt
) {
}
