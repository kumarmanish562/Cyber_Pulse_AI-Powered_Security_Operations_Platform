package cyberpulse.playbook.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ExecutePlaybookResponse(
        UUID incidentId,
        String incidentNumber,
        String action,
        List<String> stepsExecuted,
        Instant executedAt
) {
}
