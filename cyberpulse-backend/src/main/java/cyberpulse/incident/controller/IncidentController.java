package cyberpulse.incident.controller;

import cyberpulse.incident.dto.*;
import cyberpulse.incident.entity.IncidentSeverity;
import cyberpulse.incident.entity.IncidentStatus;
import cyberpulse.incident.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;


    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAuthority('INCIDENT_UPDATE')")
    public IncidentResponse create(
            @Valid @RequestBody CreateIncidentRequest request
    ) {

        return incidentService.createIncident(request);
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public Page<IncidentResponse> getAll(
            @PageableDefault(size = 20)
            Pageable pageable
    ) {

        return incidentService.getAll(pageable);
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public IncidentResponse getById(
            @PathVariable UUID id
    ) {

        return incidentService.getById(id);
    }


    // =========================================================
    // GET BY INCIDENT NUMBER
    // =========================================================

    @GetMapping("/number/{incidentNumber}")
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public IncidentResponse getByNumber(
            @PathVariable String incidentNumber
    ) {

        return incidentService.getByNumber(incidentNumber);
    }


    // =========================================================
    // GET BY STATUS
    // =========================================================

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public Page<IncidentResponse> getByStatus(
            @PathVariable IncidentStatus status,
            @PageableDefault(size = 20)
            Pageable pageable
    ) {

        return incidentService.getByStatus(
                status,
                pageable
        );
    }


    // =========================================================
    // GET BY SEVERITY
    // =========================================================

    @GetMapping("/severity/{severity}")
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public Page<IncidentResponse> getBySeverity(
            @PathVariable IncidentSeverity severity,
            @PageableDefault(size = 20)
            Pageable pageable
    ) {

        return incidentService.getBySeverity(
                severity,
                pageable
        );
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('INCIDENT_UPDATE')")
    public IncidentResponse updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIncidentStatusRequest request
    ) {

        return incidentService.updateStatus(
                id,
                request.status()
        );
    }


    // =========================================================
    // ASSIGN
    // =========================================================

    @PatchMapping("/{id}/assignment")
    @PreAuthorize("hasAuthority('INCIDENT_UPDATE')")
    public IncidentResponse assign(
            @PathVariable UUID id,
            @Valid @RequestBody AssignIncidentRequest request
    ) {

        return incidentService.assignIncident(
                id,
                request.userId()
        );
    }


    // =========================================================
    // ADD NOTE
    // =========================================================

    @PostMapping("/{id}/notes")
    @PreAuthorize("hasAuthority('INCIDENT_UPDATE')")
    public IncidentNoteResponse addNote(
            @PathVariable UUID id,
            @Valid @RequestBody AddIncidentNoteRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        return incidentService.addNote(
                id,
                username,
                request.content()
        );
    }


    // =========================================================
    // GET NOTES
    // =========================================================

    @GetMapping("/{id}/notes")
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public List<IncidentNoteResponse> getNotes(
            @PathVariable UUID id
    ) {

        return incidentService.getNotes(id);
    }


    // =========================================================
    // ADD EVENT
    // =========================================================

    @PostMapping("/{id}/events/{eventId}")
    @PreAuthorize("hasAuthority('INCIDENT_INVESTIGATE')")
    public IncidentEventResponse addEvent(
            @PathVariable UUID id,
            @PathVariable UUID eventId
    ) {

        return incidentService.addEvent(
                id,
                eventId
        );
    }


    // =========================================================
    // GET EVENTS
    // =========================================================

    @GetMapping("/{id}/events")
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public List<IncidentEventResponse> getEvents(
            @PathVariable UUID id
    ) {

        return incidentService.getEvents(id);
    }
}