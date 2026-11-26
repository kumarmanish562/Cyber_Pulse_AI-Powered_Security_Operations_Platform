package cyberpulse.simulator.service;

import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;
import cyberpulse.event.dto.CreateSecurityEventRequest;
import cyberpulse.event.service.SecurityEventService;
import cyberpulse.simulator.dto.SimulationRequest.AttackScenario;
import cyberpulse.simulator.dto.SimulationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates controlled, realistic attack telemetry bursts to validate the
 * end-to-end detection, risk scoring, incident, auto-block, and SSE pipeline.
 */
@Service
@RequiredArgsConstructor
public class AttackSimulatorService {

    private final SecurityEventService securityEventService;

    public SimulationResponse runScenario(AttackScenario scenario) {
        Instant now = Instant.now();
        List<CreateSecurityEventRequest> batch = new ArrayList<>();
        String attackerIp;
        String summary;

        switch (scenario) {
            case SSH_BRUTE_FORCE -> {
                attackerIp = "198.51.100.77";
                for (int i = 0; i < 12; i++) {
                    batch.add(new CreateSecurityEventRequest(
                            EventType.LOGIN_FAILED,
                            attackerIp,
                            "root",
                            "ssh-gateway",
                            "Simulated SSH brute-force attempt #" + (i + 1),
                            Severity.HIGH,
                            now.minusSeconds(12L - i)
                    ));
                }
                summary = "Injected 12 failed SSH logins from " + attackerIp + " to trigger BRUTE_FORCE (T1110).";
            }
            case PORT_SCAN_SWEEP -> {
                attackerIp = "203.0.113.45";
                for (int i = 0; i < 6; i++) {
                    batch.add(new CreateSecurityEventRequest(
                            EventType.PORT_SCAN,
                            attackerIp,
                            null,
                            "edge-firewall",
                            "Simulated TCP SYN sweep on port " + (20 + i),
                            Severity.HIGH,
                            now.minusSeconds(10L - i)
                    ));
                }
                summary = "Injected 6 port scan events from " + attackerIp + " to trigger PORT_SCAN (T1046).";
            }
            case IMPOSSIBLE_TRAVEL -> {
                attackerIp = "185.220.101.4";
                List<String> ips = List.of("45.33.32.156", attackerIp, "91.198.174.192");
                for (int i = 0; i < ips.size(); i++) {
                    batch.add(new CreateSecurityEventRequest(
                            EventType.LOGIN_SUCCESS,
                            ips.get(i),
                            "soc_executive",
                            "sso-portal",
                            "Simulated multi-region login from " + ips.get(i),
                            Severity.MEDIUM,
                            now.minusSeconds(30L - (i * 5L))
                    ));
                }
                summary = "Injected 3 multi-IP logins for soc_executive to trigger SUSPICIOUS_LOGIN (T1078).";
            }
            default -> throw new IllegalArgumentException("Unsupported scenario: " + scenario);
        }

        securityEventService.createBatch(batch);

        return new SimulationResponse(
                scenario.name(),
                batch.size(),
                attackerIp,
                summary,
                Instant.now()
        );
    }
}
