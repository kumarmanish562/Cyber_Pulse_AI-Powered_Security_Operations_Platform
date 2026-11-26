package cyberpulse.simulator.dto;

import jakarta.validation.constraints.NotNull;

public record SimulationRequest(
        @NotNull(message = "Scenario is required")
        AttackScenario scenario
) {
    public enum AttackScenario {
        SSH_BRUTE_FORCE,
        PORT_SCAN_SWEEP,
        IMPOSSIBLE_TRAVEL
    }
}
