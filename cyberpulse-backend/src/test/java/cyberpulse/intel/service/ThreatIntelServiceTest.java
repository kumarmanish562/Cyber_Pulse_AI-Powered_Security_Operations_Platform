package cyberpulse.intel.service;

import cyberpulse.blocklist.entity.BlockedIp;
import cyberpulse.blocklist.repository.BlockedIpRepository;
import cyberpulse.common.enums.EventType;
import cyberpulse.event.repository.SecurityEventRepository;
import cyberpulse.intel.dto.IpThreatIntelResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.net.InetAddress;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThreatIntelServiceTest {

    @Mock
    private SecurityEventRepository eventRepository;

    @Mock
    private BlockedIpRepository blockedIpRepository;

    @Test
    void shouldClassifyBlockedIpAsMalicious() throws Exception {
        ThreatIntelService service = new ThreatIntelService(
                eventRepository,
                blockedIpRepository,
                new ObjectMapper(),
                ""
        );

        InetAddress addr = InetAddress.getByName("198.51.100.25");
        when(eventRepository.countBySourceIp(addr)).thenReturn(15L);
        when(eventRepository.countBySourceIpAndEventType(addr, EventType.LOGIN_FAILED)).thenReturn(12L);
        when(eventRepository.countBySourceIpAndEventType(addr, EventType.PORT_SCAN)).thenReturn(0L);

        BlockedIp blocked = new BlockedIp();
        blocked.setActive(true);
        blocked.setReason("Brute force");
        when(blockedIpRepository.findByIpAddress("198.51.100.25")).thenReturn(Optional.of(blocked));

        IpThreatIntelResponse res = service.lookupIp("198.51.100.25");

        assertEquals("MALICIOUS", res.riskVerdict());
        assertTrue(res.currentlyBlocked());
        assertEquals(15L, res.totalEvents());
        assertEquals("INTERNAL_TELEMETRY", res.externalProvider());
    }

    @Test
    void shouldRejectInvalidIpAddress() {
        ThreatIntelService service = new ThreatIntelService(
                eventRepository,
                blockedIpRepository,
                new ObjectMapper(),
                ""
        );

        assertThrows(IllegalArgumentException.class, () -> service.lookupIp("not-an-ip-host-invalid-%%%"));
    }
}
