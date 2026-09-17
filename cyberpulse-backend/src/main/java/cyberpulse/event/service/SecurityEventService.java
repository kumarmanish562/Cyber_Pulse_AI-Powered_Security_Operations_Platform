package cyberpulse.event.service;

import cyberpulse.detection.engine.DetectionEngine;
import cyberpulse.event.dto.CreateSecurityEventRequest;
import cyberpulse.event.dto.SecurityEventFilter;
import cyberpulse.event.dto.SecurityEventResponse;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.event.mapper.SecurityEventMapper;
import cyberpulse.event.repository.SecurityEventRepository;
import cyberpulse.event.specification.SecurityEventSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class SecurityEventService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final SecurityEventRepository eventRepository;
    private final SecurityEventMapper eventMapper;

    private final DetectionEngine detectionEngine;


    public SecurityEventService(
            SecurityEventRepository eventRepository,
            SecurityEventMapper eventMapper,
            DetectionEngine detectionEngine
    ) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.detectionEngine = detectionEngine;
    }

    @Transactional
    public SecurityEventResponse createEvent(
            CreateSecurityEventRequest request
    ) {

        validateTimeRange(request.eventTime());

        SecurityEvent event =
                eventMapper.toEntity(request);

        SecurityEvent savedEvent =
                eventRepository.save(event);

        detectionEngine.analyze(savedEvent);

        return eventMapper.toResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public Page<SecurityEventResponse> getEvents(
            SecurityEventFilter filter,
            int page,
            int size
    ) {

        int safeSize = normalizePageSize(size);

        PageRequest pageRequest =
                PageRequest.of(
                        page,
                        safeSize,
                        Sort.by(
                                Sort.Direction.DESC,
                                "eventTime"
                        )
                );

        return eventRepository
                .findAll(
                        SecurityEventSpecification
                                .withFilter(filter),
                        pageRequest
                )
                .map(eventMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SecurityEventResponse getEvent(UUID id) {

        SecurityEvent event =
                eventRepository.findById(id)
                        .orElseThrow(
                                () -> new EventNotFoundException(
                                        "Security event not found: " + id
                                )
                        );

        return eventMapper.toResponse(event);
    }

    private void validateTimeRange(
            Instant eventTime
    ) {

        if (eventTime.isAfter(
                Instant.now().plusSeconds(60)
        )) {

            throw new IllegalArgumentException(
                    "Event time cannot be more than 60 seconds in the future"
            );
        }
    }

    private int normalizePageSize(int size) {

        if (size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }

        return Math.min(size, MAX_PAGE_SIZE);
    }

    public static class EventNotFoundException
            extends RuntimeException {

        public EventNotFoundException(String message) {
            super(message);
        }
    }
}