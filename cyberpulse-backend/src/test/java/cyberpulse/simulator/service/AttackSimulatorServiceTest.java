package cyberpulse.simulator.service;

import cyberpulse.event.service.SecurityEventService;
import cyberpulse.simulator.dto.SimulationRequest.AttackScenario;
import cyberpulse.simulator.dto.SimulationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttackSimulatorServiceTest {

    @Mock
    private SecurityEventService securityEventService;

    @InjectMocks
    private AttackSimulatorService simulatorService;

    @Test
    void shouldInjectBruteForceBurst() {
        when(securityEventService.createBatch(anyList())).thenReturn(List.of());

        SimulationResponse res = simulatorService.runScenario(AttackScenario.SSH_BRUTE_FORCE);

        assertEquals("SSH_BRUTE_FORCE", res.scenario());
        assertEquals(12, res.eventsInjected());
        assertEquals("198.51.100.77", res.attackerIp());
        verify(securityEventService).createBatch(argThat(list -> list.size() == 12));
    }

    @Test
    void shouldInjectPortScanSweep() {
        when(securityEventService.createBatch(anyList())).thenReturn(List.of());

        SimulationResponse res = simulatorService.runScenario(AttackScenario.PORT_SCAN_SWEEP);

        assertEquals(6, res.eventsInjected());
        verify(securityEventService).createBatch(argThat(list -> list.size() == 6));
    }
}
