package cyberpulse.event.controller;

import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;
import cyberpulse.common.validation.IpAddress;
import cyberpulse.event.dto.CreateSecurityEventRequest;
import cyberpulse.event.dto.SecurityEventFilter;
import cyberpulse.event.dto.SecurityEventResponse;
import cyberpulse.event.service.SecurityEventService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@Validated
public class SecurityEventController {

    private final SecurityEventService eventService;

    public SecurityEventController(
            SecurityEventService eventService
    ) {
        this.eventService = eventService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EVENT_WRITE')")
    public ResponseEntity<SecurityEventResponse> createEvent(
            @Valid @RequestBody
            CreateSecurityEventRequest request
    ) {

        SecurityEventResponse response =
                eventService.createEvent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public ResponseEntity<Page<SecurityEventResponse>> getEvents(

            @RequestParam(required = false)
            EventType eventType,

            @RequestParam(required = false)
            Severity severity,

            @RequestParam(required = false)
            @IpAddress
            String sourceIp,

            @RequestParam(required = false)
            String username,

            @RequestParam(required = false)
            String service,

            @RequestParam(required = false)
            Instant from,

            @RequestParam(required = false)
            Instant to,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be >= 0")
            int page,

            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "Size must be >= 1")
            @Max(value = 100, message = "Size must be <= 100")
            int size
    ) {

        SecurityEventFilter filter =
                new SecurityEventFilter(
                        eventType,
                        severity,
                        sourceIp,
                        username,
                        service,
                        from,
                        to
                );

        Page<SecurityEventResponse> events =
                eventService.getEvents(
                        filter,
                        page,
                        size
                );

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public ResponseEntity<SecurityEventResponse> getEvent(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                eventService.getEvent(id)
        );
    }
}