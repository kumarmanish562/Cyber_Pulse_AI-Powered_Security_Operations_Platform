package cyberpulse.risk.event;

import java.util.UUID;

public record RiskAssessmentCreatedEvent(
        UUID riskAssessmentId
) {
}