package cyberpulse.report.service;

import cyberpulse.blocklist.service.BlocklistService;
import cyberpulse.detection.repository.ThreatDetectionRepository;
import cyberpulse.event.repository.SecurityEventRepository;
import cyberpulse.report.dto.ShiftHandoverReport;
import cyberpulse.soc.dto.SocPostureResponse;
import cyberpulse.soc.service.SocPostureService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftHandoverServiceTest {

    @Mock
    private SecurityEventRepository eventRepository;
    @Mock
    private ThreatDetectionRepository detectionRepository;
    @Mock
    private BlocklistService blocklistService;
    @Mock
    private SocPostureService socPostureService;

    @InjectMocks
    private ShiftHandoverService service;

    @Test
    void shouldGenerateMarkdownAndComplianceRate() {
        when(eventRepository.findAll()).thenReturn(List.of());
        when(detectionRepository.findAll()).thenReturn(List.of());
        when(blocklistService.getActiveBlocklist()).thenReturn(List.of());
        when(socPostureService.getPosture()).thenReturn(new SocPostureResponse(
                4L,
                1L,
                0L,
                List.of(),
                List.of(new SocPostureResponse.MitreTechniqueSummary("TA0006", "Credential Access", "T1110", "Brute Force", 5L))
        ));

        ShiftHandoverReport report = service.generate24HourReport();

        assertEquals(24, report.windowHours());
        assertEquals(75.0, report.slaCompliancePercent());
        assertTrue(report.markdownSummary().contains("CyberPulse SOC Shift Handover Report"));
        assertTrue(report.markdownSummary().contains("T1110"));
    }
}
