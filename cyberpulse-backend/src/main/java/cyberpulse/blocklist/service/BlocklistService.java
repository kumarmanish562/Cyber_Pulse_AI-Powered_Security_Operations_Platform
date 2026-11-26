package cyberpulse.blocklist.service;

import cyberpulse.blocklist.dto.BlockIpRequest;
import cyberpulse.blocklist.dto.BlockedIpResponse;
import cyberpulse.blocklist.entity.BlockedIp;
import cyberpulse.blocklist.repository.BlockedIpRepository;
import cyberpulse.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlocklistService {

    private final BlockedIpRepository repository;

    @Transactional
    public BlockedIpResponse blockIp(BlockIpRequest request, UUID createdBy) {
        return blockIpInternal(
                request.ipAddress().trim(),
                request.reason().trim(),
                "MANUAL",
                request.severity() != null ? request.severity().name() : "HIGH",
                request.expiresAt(),
                createdBy
        );
    }

    @Transactional
    public BlockedIpResponse blockIpAutomatically(
            String ipAddress,
            String reason,
            String severity,
            Instant expiresAt
    ) {
        return blockIpInternal(ipAddress, reason, "AUTO_DETECTION", severity, expiresAt, null);
    }

    private BlockedIpResponse blockIpInternal(
            String ipAddress,
            String reason,
            String source,
            String severity,
            Instant expiresAt,
            UUID createdBy
    ) {
        BlockedIp entity = repository.findByIpAddress(ipAddress).orElseGet(BlockedIp::new);
        entity.setIpAddress(ipAddress);
        entity.setReason(reason);
        entity.setSource(source);
        entity.setSeverity(severity);
        entity.setActive(true);
        entity.setExpiresAt(expiresAt);
        if (createdBy != null) {
            entity.setCreatedBy(createdBy);
        }

        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<BlockedIpResponse> getActiveBlocklist() {
        Instant now = Instant.now();
        return repository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .filter(b -> b.getExpiresAt() == null || b.getExpiresAt().isAfter(now))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public String exportPlaintextFeed() {
        return getActiveBlocklist().stream()
                .map(BlockedIpResponse::ipAddress)
                .collect(Collectors.joining("\n"));
    }

    @Transactional
    public void unblockIp(UUID id) {
        BlockedIp entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blocked IP not found: " + id));
        entity.setActive(false);
        repository.save(entity);
    }

    private BlockedIpResponse toResponse(BlockedIp b) {
        return new BlockedIpResponse(
                b.getId(),
                b.getIpAddress(),
                b.getReason(),
                b.getSource(),
                b.getSeverity(),
                b.isActive(),
                b.getExpiresAt(),
                b.getCreatedAt()
        );
    }
}
