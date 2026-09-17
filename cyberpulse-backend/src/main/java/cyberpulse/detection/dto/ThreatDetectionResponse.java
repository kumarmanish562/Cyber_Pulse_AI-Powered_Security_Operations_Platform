package cyberpulse.detection.dto;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ThreatDetectionResponse(

        UUID id,

        UUID ruleId,

        String ruleName,

        UUID eventId,

        String threatType,

        BigDecimal confidenceScore,

        Map<String, Object> details,

        Instant createdAt
) {
}
