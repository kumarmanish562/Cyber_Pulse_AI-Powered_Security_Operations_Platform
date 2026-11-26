package cyberpulse.apikey.service;

import cyberpulse.apikey.dto.ApiKeyResponse;
import cyberpulse.apikey.dto.CreateApiKeyRequest;
import cyberpulse.apikey.dto.CreateApiKeyResponse;
import cyberpulse.apikey.entity.ServiceApiKey;
import cyberpulse.apikey.repository.ServiceApiKeyRepository;
import cyberpulse.common.exception.BusinessException;
import cyberpulse.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceApiKeyService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final ServiceApiKeyRepository repository;

    @Transactional
    public CreateApiKeyResponse createKey(CreateApiKeyRequest request, UUID createdBy) {
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCase(name)) {
            throw new BusinessException("API key with this name already exists: " + name);
        }

        byte[] randomBytes = new byte[24];
        RANDOM.nextBytes(randomBytes);
        String rawKey = "cp_live_" + HexFormat.of().formatHex(randomBytes);
        String prefix = rawKey.substring(0, 14);
        String hash = sha256Hex(rawKey);

        ServiceApiKey entity = new ServiceApiKey();
        entity.setName(name);
        entity.setKeyPrefix(prefix);
        entity.setKeyHash(hash);
        entity.setScopes(request.scopes() != null && !request.scopes().isBlank()
                ? request.scopes().trim()
                : "EVENT_WRITE");
        entity.setActive(true);
        entity.setCreatedBy(createdBy);
        entity.setExpiresAt(request.expiresAt());

        ServiceApiKey saved = repository.save(entity);
        return new CreateApiKeyResponse(toResponse(saved), rawKey);
    }

    @Transactional(readOnly = true)
    public List<ApiKeyResponse> listKeys() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void revokeKey(UUID id) {
        ServiceApiKey key = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("API key not found: " + id));
        key.setActive(false);
        repository.save(key);
    }

    @Transactional
    public Optional<ServiceApiKey> authenticate(String rawKey) {
        if (rawKey == null || rawKey.isBlank()) {
            return Optional.empty();
        }
        String hash = sha256Hex(rawKey.trim());
        Optional<ServiceApiKey> match = repository.findByKeyHashAndActiveTrue(hash);
        if (match.isEmpty()) {
            return Optional.empty();
        }

        ServiceApiKey key = match.get();
        if (key.getExpiresAt() != null && key.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }

        key.setLastUsedAt(Instant.now());
        repository.save(key);
        return Optional.of(key);
    }

    static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private ApiKeyResponse toResponse(ServiceApiKey k) {
        return new ApiKeyResponse(
                k.getId(),
                k.getName(),
                k.getKeyPrefix(),
                k.getScopes(),
                k.isActive(),
                k.getLastUsedAt(),
                k.getExpiresAt(),
                k.getCreatedAt()
        );
    }
}
