package cyberpulse.ai.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AiTriageResponse(
        UUID incidentId,
        String incidentNumber,
        String provider,
        String summary,
        String likelyAttackVector,
        List<String> recommendedActions,
        String priorityAssessment,
        Instant generatedAt
) {
}
