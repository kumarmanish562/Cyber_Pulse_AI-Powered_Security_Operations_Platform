package cyberpulse.blocklist.service;

import cyberpulse.blocklist.dto.BlockIpRequest;
import cyberpulse.blocklist.dto.BlockedIpResponse;
import cyberpulse.blocklist.entity.BlockedIp;
import cyberpulse.blocklist.repository.BlockedIpRepository;
import cyberpulse.common.enums.Severity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlocklistServiceTest {

    @Mock
    private BlockedIpRepository repository;

    @InjectMocks
    private BlocklistService service;

    @Test
    void shouldBlockIpAndExportPlaintextFeed() {
        when(repository.findByIpAddress("203.0.113.77")).thenReturn(Optional.empty());
        when(repository.save(any(BlockedIp.class))).thenAnswer(inv -> {
            BlockedIp b = inv.getArgument(0);
            b.setId(UUID.randomUUID());
            b.setCreatedAt(Instant.now());
            return b;
        });

        BlockedIpResponse res = service.blockIp(
                new BlockIpRequest("203.0.113.77", "Credential stuffing", Severity.CRITICAL, null),
                UUID.randomUUID()
        );

        assertEquals("203.0.113.77", res.ipAddress());
        assertEquals("MANUAL", res.source());
        assertTrue(res.active());

        BlockedIp active = new BlockedIp();
        active.setId(res.id());
        active.setIpAddress("203.0.113.77");
        active.setActive(true);
        active.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));

        when(repository.findByActiveTrueOrderByCreatedAtDesc()).thenReturn(List.of(active));
        assertEquals("203.0.113.77", service.exportPlaintextFeed());
    }

    @Test
    void shouldUnblockExistingIp() {
        UUID id = UUID.randomUUID();
        BlockedIp existing = new BlockedIp();
        existing.setId(id);
        existing.setActive(true);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        service.unblockIp(id);

        assertFalse(existing.isActive());
    }
}
