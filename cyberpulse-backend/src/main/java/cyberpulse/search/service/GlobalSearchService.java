package cyberpulse.search.service;

import cyberpulse.blocklist.dto.BlockedIpResponse;
import cyberpulse.blocklist.repository.BlockedIpRepository;
import cyberpulse.event.mapper.SecurityEventMapper;
import cyberpulse.event.repository.SecurityEventRepository;
import cyberpulse.incident.mapper.IncidentMapper;
import cyberpulse.incident.repository.IncidentRepository;
import cyberpulse.search.dto.GlobalSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalSearchService {

    private final IncidentRepository incidentRepository;
    private final SecurityEventRepository eventRepository;
    private final BlockedIpRepository blockedIpRepository;
    private final IncidentMapper incidentMapper;
    private final SecurityEventMapper eventMapper;

    @Transactional(readOnly = true)
    public GlobalSearchResponse search(String rawQuery) {
        String query = rawQuery != null ? rawQuery.trim() : "";
        if (query.isEmpty()) {
            return new GlobalSearchResponse(query, List.of(), List.of(), List.of());
        }

        var incidents = incidentRepository
                .findTop5ByTitleContainingIgnoreCaseOrIncidentNumberContainingIgnoreCaseOrderByCreatedAtDesc(
                        query, query
                )
                .stream()
                .map(incidentMapper::toResponse)
                .toList();

        var events = eventRepository
                .findTop5ByUsernameContainingIgnoreCaseOrServiceContainingIgnoreCaseOrderByEventTimeDesc(
                        query, query
                )
                .stream()
                .map(eventMapper::toResponse)
                .toList();

        var blockedIps = blockedIpRepository
                .findTop5ByIpAddressContainingIgnoreCaseOrReasonContainingIgnoreCaseOrderByCreatedAtDesc(
                        query, query
                )
                .stream()
                .map(b -> new BlockedIpResponse(
                        b.getId(),
                        b.getIpAddress(),
                        b.getReason(),
                        b.getSource(),
                        b.getSeverity(),
                        b.isActive(),
                        b.getExpiresAt(),
                        b.getCreatedAt()
                ))
                .toList();

        return new GlobalSearchResponse(query, incidents, events, blockedIps);
    }
}
