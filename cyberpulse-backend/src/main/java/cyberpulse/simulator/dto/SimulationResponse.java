package cyberpulse.simulator.dto;

import java.time.Instant;

public record SimulationResponse(
        String scenario,
        int eventsInjected,
        String attackerIp,
        String summary,
        Instant triggeredAt
) {
}
