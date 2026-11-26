package cyberpulse.search.dto;

import cyberpulse.blocklist.dto.BlockedIpResponse;
import cyberpulse.event.dto.SecurityEventResponse;
import cyberpulse.incident.dto.IncidentResponse;

import java.util.List;

public record GlobalSearchResponse(
        String query,
        List<IncidentResponse> incidents,
        List<SecurityEventResponse> events,
        List<BlockedIpResponse> blockedIps
) {
}
