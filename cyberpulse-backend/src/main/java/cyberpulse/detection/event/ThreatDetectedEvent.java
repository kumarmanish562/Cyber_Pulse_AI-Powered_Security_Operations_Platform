package cyberpulse.detection.event;

import java.util.UUID;

public record ThreatDetectedEvent(
        UUID threatDetectionId
) {
}