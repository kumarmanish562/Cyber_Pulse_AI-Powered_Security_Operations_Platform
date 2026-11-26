package cyberpulse.apikey.service;

import cyberpulse.apikey.dto.CreateApiKeyRequest;
import cyberpulse.apikey.dto.CreateApiKeyResponse;
import cyberpulse.apikey.entity.ServiceApiKey;
import cyberpulse.apikey.repository.ServiceApiKeyRepository;
import cyberpulse.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceApiKeyServiceTest {

    @Mock
    private ServiceApiKeyRepository repository;

    @InjectMocks
    private ServiceApiKeyService service;

    @Test
    void shouldCreateAndAuthenticateApiKey() {
        when(repository.existsByNameIgnoreCase("fluentbit-prod")).thenReturn(false);
        when(repository.save(any(ServiceApiKey.class))).thenAnswer(inv -> {
            ServiceApiKey k = inv.getArgument(0);
            k.setId(UUID.randomUUID());
            k.setCreatedAt(Instant.now());
            return k;
        });

        CreateApiKeyResponse created = service.createKey(
                new CreateApiKeyRequest("fluentbit-prod", "EVENT_WRITE", null),
                UUID.randomUUID()
        );

        assertTrue(created.rawKey().startsWith("cp_live_"));
        assertEquals("EVENT_WRITE", created.apiKey().scopes());

        ServiceApiKey stored = new ServiceApiKey();
        stored.setName("fluentbit-prod");
        stored.setScopes("EVENT_WRITE");
        stored.setActive(true);
        stored.setKeyHash(ServiceApiKeyService.sha256Hex(created.rawKey()));

        when(repository.findByKeyHashAndActiveTrue(stored.getKeyHash())).thenReturn(Optional.of(stored));
        assertTrue(service.authenticate(created.rawKey()).isPresent());
    }

    @Test
    void shouldRejectDuplicateKeyName() {
        when(repository.existsByNameIgnoreCase("dup")).thenReturn(true);
        assertThrows(
                BusinessException.class,
                () -> service.createKey(new CreateApiKeyRequest("dup", null, null), UUID.randomUUID())
        );
    }
}
