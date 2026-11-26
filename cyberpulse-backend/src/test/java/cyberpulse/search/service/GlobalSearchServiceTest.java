package cyberpulse.search.service;

import cyberpulse.blocklist.repository.BlockedIpRepository;
import cyberpulse.event.mapper.SecurityEventMapper;
import cyberpulse.event.repository.SecurityEventRepository;
import cyberpulse.incident.mapper.IncidentMapper;
import cyberpulse.incident.repository.IncidentRepository;
import cyberpulse.search.dto.GlobalSearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalSearchServiceTest {

    @Mock
    private IncidentRepository incidentRepository;
    @Mock
    private SecurityEventRepository eventRepository;
    @Mock
    private BlockedIpRepository blockedIpRepository;
    @Mock
    private IncidentMapper incidentMapper;
    @Mock
    private SecurityEventMapper eventMapper;

    @InjectMocks
    private GlobalSearchService service;

    @Test
    void shouldReturnEmptyListsForBlankQuery() {
        GlobalSearchResponse res = service.search("   ");
        assertTrue(res.incidents().isEmpty());
        assertTrue(res.events().isEmpty());
        assertTrue(res.blockedIps().isEmpty());
        verifyNoInteractions(incidentRepository, eventRepository, blockedIpRepository);
    }

    @Test
    void shouldQueryAllThreeRepositoriesForNonBlankQuery() {
        when(incidentRepository.findTop5ByTitleContainingIgnoreCaseOrIncidentNumberContainingIgnoreCaseOrderByCreatedAtDesc("ssh", "ssh"))
                .thenReturn(List.of());
        when(eventRepository.findTop5ByUsernameContainingIgnoreCaseOrServiceContainingIgnoreCaseOrderByEventTimeDesc("ssh", "ssh"))
                .thenReturn(List.of());
        when(blockedIpRepository.findTop5ByIpAddressContainingIgnoreCaseOrReasonContainingIgnoreCaseOrderByCreatedAtDesc("ssh", "ssh"))
                .thenReturn(List.of());

        GlobalSearchResponse res = service.search("ssh");
        assertEquals("ssh", res.query());
    }
}
