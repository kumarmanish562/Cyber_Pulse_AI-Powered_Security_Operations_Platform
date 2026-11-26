package cyberpulse.soc.service;

import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.detection.repository.ThreatDetectionRepository;
import cyberpulse.incident.entity.Incident;
import cyberpulse.incident.entity.IncidentSeverity;
import cyberpulse.incident.entity.IncidentStatus;
import cyberpulse.incident.repository.IncidentRepository;
import cyberpulse.soc.dto.SocPostureResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SocPostureServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private ThreatDetectionRepository detectionRepository;

    @Test
    void shouldDetectSlaBreachAndMapMitreTechniques() {
        Incident criticalBreached = new Incident();
        criticalBreached.setId(UUID.randomUUID());
        criticalBreached.setIncidentNumber("INC-2026-0099");
        criticalBreached.setTitle("Critical brute force");
        criticalBreached.setSeverity(IncidentSeverity.CRITICAL);
        criticalBreached.setStatus(IncidentStatus.OPEN);
        criticalBreached.setCreatedAt(Instant.now().minus(30, ChronoUnit.MINUTES));

        ThreatDetection d1 = new ThreatDetection();
        d1.setThreatType("BRUTE_FORCE");

        ThreatDetection d2 = new ThreatDetection();
        d2.setThreatType("PORT_SCAN");

        when(incidentRepository.findAll()).thenReturn(List.of(criticalBreached));
        when(detectionRepository.findAll()).thenReturn(List.of(d1, d2));

        SocPostureService service = new SocPostureService(
                incidentRepository,
                detectionRepository,
                new MitreMappingService()
        );

        SocPostureResponse posture = service.getPosture();

        assertEquals(1L, posture.openIncidents());
        assertEquals(1L, posture.slaBreachedCount());
        assertTrue(posture.slaAlerts().getFirst().breached());
        assertEquals(2, posture.mitreTechniques().size());
        assertTrue(posture.mitreTechniques().stream().anyMatch(m -> "T1110".equals(m.techniqueId())));
        assertTrue(posture.mitreTechniques().stream().anyMatch(m -> "T1046".equals(m.techniqueId())));
    }
}
