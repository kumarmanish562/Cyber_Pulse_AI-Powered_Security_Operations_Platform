package cyberpulse.incident.service;

import cyberpulse.common.exception.ResourceNotFoundException;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.event.repository.SecurityEventRepository;
import cyberpulse.incident.dto.CreateIncidentRequest;
import cyberpulse.incident.dto.IncidentEventResponse;
import cyberpulse.incident.dto.IncidentNoteResponse;
import cyberpulse.incident.dto.IncidentResponse;
import cyberpulse.incident.entity.Incident;
import cyberpulse.incident.entity.IncidentEvent;
import cyberpulse.incident.entity.IncidentNote;
import cyberpulse.incident.entity.IncidentSeverity;
import cyberpulse.incident.entity.IncidentStatus;
import cyberpulse.incident.mapper.IncidentEventMapper;
import cyberpulse.incident.mapper.IncidentMapper;
import cyberpulse.incident.mapper.IncidentNoteMapper;
import cyberpulse.incident.repository.IncidentEventRepository;
import cyberpulse.incident.repository.IncidentNoteRepository;
import cyberpulse.incident.repository.IncidentRepository;
import cyberpulse.risk.entity.RiskAssessment;
import cyberpulse.risk.repository.RiskAssessmentRepository;
import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final IncidentEventRepository incidentEventRepository;
    private final IncidentNoteRepository incidentNoteRepository;
    private final SecurityEventRepository securityEventRepository;
    private final UserRepository userRepository;
    private final IncidentNumberGenerator incidentNumberGenerator;
    private final IncidentMapper incidentMapper;
    private final IncidentEventMapper incidentEventMapper;
    private final IncidentNoteMapper incidentNoteMapper;


    // =========================================================
    // CREATE INCIDENT
    // =========================================================

    @Transactional
    public IncidentResponse createIncident(
            CreateIncidentRequest request
    ) {

        UUID riskAssessmentId = request.riskAssessmentId();

        /*
         * One RiskAssessment can create only one Incident.
         */
        if (incidentRepository.existsByRiskAssessmentId(
                riskAssessmentId
        )) {

            throw new IllegalStateException(
                    "An incident already exists for this risk assessment"
            );
        }

        /*
         * Load the RiskAssessment.
         */
        RiskAssessment assessment =
                riskAssessmentRepository
                        .findById(riskAssessmentId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Risk assessment not found: "
                                                + riskAssessmentId
                                )
                        );

        /*
         * Create Incident.
         */
        Incident incident = new Incident();

        incident.setIncidentNumber(
                incidentNumberGenerator.next()
        );

        incident.setTitle(
                request.title()
        );

        incident.setDescription(
                request.description()
        );

        incident.setStatus(
                IncidentStatus.OPEN
        );

        incident.setSeverity(
                mapSeverity(assessment)
        );

        incident.setRiskAssessment(
                assessment
        );

        /*
         * Save.
         */
        Incident savedIncident =
                incidentRepository.save(incident);

        return incidentMapper.toResponse(
                savedIncident
        );
    }


    // =========================================================
    // GET INCIDENT BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public IncidentResponse getById(
            UUID incidentId
    ) {

        Incident incident =
                findIncident(incidentId);

        return incidentMapper.toResponse(
                incident
        );
    }


    // =========================================================
    // GET INCIDENT BY NUMBER
    // =========================================================

    @Transactional(readOnly = true)
    public IncidentResponse getByNumber(
            String incidentNumber
    ) {

        Incident incident =
                incidentRepository
                        .findByIncidentNumber(incidentNumber)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Incident not found: "
                                                + incidentNumber
                                )
                        );

        return incidentMapper.toResponse(
                incident
        );
    }


    // =========================================================
    // GET ALL INCIDENTS
    // =========================================================

    @Transactional(readOnly = true)
    public Page<IncidentResponse> getAll(
            Pageable pageable
    ) {

        return incidentRepository
                .findAll(pageable)
                .map(incidentMapper::toResponse);
    }


    // =========================================================
    // GET INCIDENTS BY STATUS
    // =========================================================

    @Transactional(readOnly = true)
    public Page<IncidentResponse> getByStatus(
            IncidentStatus status,
            Pageable pageable
    ) {

        return incidentRepository
                .findByStatus(
                        status,
                        pageable
                )
                .map(incidentMapper::toResponse);
    }


    // =========================================================
    // GET INCIDENTS BY SEVERITY
    // =========================================================

    @Transactional(readOnly = true)
    public Page<IncidentResponse> getBySeverity(
            IncidentSeverity severity,
            Pageable pageable
    ) {

        return incidentRepository
                .findBySeverity(
                        severity,
                        pageable
                )
                .map(incidentMapper::toResponse);
    }


    // =========================================================
    // UPDATE INCIDENT STATUS
    // =========================================================

    @Transactional
    public IncidentResponse updateStatus(
            UUID incidentId,
            IncidentStatus newStatus
    ) {

        Incident incident =
                findIncident(incidentId);

        IncidentStatus currentStatus =
                incident.getStatus();

        /*
         * Validate lifecycle transition.
         */
        validateTransition(
                currentStatus,
                newStatus
        );

        Instant now =
                Instant.now();

        /*
         * Update status.
         */
        incident.setStatus(
                newStatus
        );

        /*
         * RESOLVED timestamp.
         */
        if (newStatus == IncidentStatus.RESOLVED) {

            incident.setResolvedAt(
                    now
            );
        }

        /*
         * CLOSED timestamp.
         */
        if (newStatus == IncidentStatus.CLOSED) {

            if (incident.getResolvedAt() == null) {

                throw new IllegalStateException(
                        "Incident must be resolved before closing"
                );
            }

            incident.setClosedAt(
                    now
            );
        }

        Incident savedIncident =
                incidentRepository.save(
                        incident
                );

        return incidentMapper.toResponse(
                savedIncident
        );
    }


    // =========================================================
    // ASSIGN INCIDENT
    // =========================================================

    @Transactional
    public IncidentResponse assignIncident(
            UUID incidentId,
            UUID userId
    ) {

        Incident incident =
                findIncident(incidentId);

        /*
         * Closed incidents cannot be assigned.
         */
        if (incident.getStatus()
                == IncidentStatus.CLOSED) {

            throw new IllegalStateException(
                    "Closed incidents cannot be assigned"
            );
        }

        /*
         * Make sure the user exists.
         */
        userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found: "
                                        + userId
                        )
                );

        /*
         * Incident.assignedTo is UUID
         * in your current entity.
         */
        incident.setAssignedTo(
                userId
        );

        Incident savedIncident =
                incidentRepository.save(
                        incident
                );

        return incidentMapper.toResponse(
                savedIncident
        );
    }


    // =========================================================
    // ADD NOTE
    // =========================================================

    @Transactional
    public IncidentNoteResponse addNote(
            UUID incidentId,
            String username,
            String noteContent
    ) {

        /*
         * Find incident.
         */
        Incident incident =
                findIncident(incidentId);

        /*
         * Closed incidents cannot receive notes.
         */
        if (incident.getStatus()
                == IncidentStatus.CLOSED) {

            throw new IllegalStateException(
                    "Closed incident cannot receive notes"
            );
        }

        /*
         * IMPORTANT:
         *
         * JWT subject = username.
         *
         * Therefore:
         *
         * authentication.getName()
         *          ↓
         * username
         *          ↓
         * findByUsernameIgnoreCase()
         *
         * Do NOT use UUID.fromString(username).
         */
        User user =
                userRepository
                        .findByUsernameIgnoreCase(
                                username
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found: "
                                                + username
                                )
                        );

        /*
         * Create note.
         */
        IncidentNote incidentNote =
                new IncidentNote();

        incidentNote.setIncident(
                incident
        );

        incidentNote.setUser(
                user
        );

        incidentNote.setNote(
                noteContent
        );

        /*
         * createdAt is automatically populated
         * by @PrePersist in IncidentNote.
         */
        IncidentNote savedNote =
                incidentNoteRepository.save(
                        incidentNote
                );

        return incidentNoteMapper.toResponse(
                savedNote
        );
    }


    // =========================================================
    // GET INCIDENT NOTES
    // =========================================================

    @Transactional(readOnly = true)
    public List<IncidentNoteResponse> getNotes(
            UUID incidentId
    ) {

        /*
         * Make sure incident exists.
         */
        findIncident(incidentId);

        return incidentNoteRepository
                .findByIncidentIdOrderByCreatedAtAsc(
                        incidentId
                )
                .stream()
                .map(incidentNoteMapper::toResponse)
                .toList();
    }


    // =========================================================
    // ADD SECURITY EVENT TO INCIDENT
    // =========================================================

    @Transactional
    public IncidentEventResponse addEvent(
            UUID incidentId,
            UUID eventId
    ) {

        /*
         * Find incident.
         */
        Incident incident =
                findIncident(incidentId);

        /*
         * Find security event.
         */
        SecurityEvent event =
                securityEventRepository
                        .findById(eventId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Security event not found: "
                                                + eventId
                                )
                        );

        /*
         * Prevent duplicate event linkage.
         */
        if (incidentEventRepository
                .existsByIncident_IdAndEvent_Id(
                        incidentId,
                        eventId
                )) {

            throw new IllegalStateException(
                    "Event is already associated with this incident"
            );
        }

        /*
         * Create relationship.
         */
        IncidentEvent incidentEvent =
                new IncidentEvent();

        incidentEvent.setIncident(
                incident
        );

        incidentEvent.setEvent(
                event
        );

        /*
         * createdAt is populated by
         * IncidentEvent @PrePersist.
         */
        IncidentEvent savedEvent =
                incidentEventRepository.save(
                        incidentEvent
                );

        return incidentEventMapper.toResponse(
                savedEvent
        );
    }


    // =========================================================
    // GET INCIDENT EVENTS
    // =========================================================

    @Transactional(readOnly = true)
    public List<IncidentEventResponse> getEvents(
            UUID incidentId
    ) {

        /*
         * Make sure incident exists.
         */
        findIncident(incidentId);

        return incidentEventRepository
                .findByIncident_Id(
                        incidentId
                )
                .stream()
                .map(incidentEventMapper::toResponse)
                .toList();
    }


    // =========================================================
    // FIND INCIDENT
    // =========================================================

    private Incident findIncident(
            UUID incidentId
    ) {

        return incidentRepository
                .findById(incidentId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Incident not found: "
                                        + incidentId
                        )
                );
    }


    // =========================================================
    // RISK SEVERITY → INCIDENT SEVERITY
    // =========================================================

    private IncidentSeverity mapSeverity(
            RiskAssessment assessment
    ) {

        if (assessment.getSeverity() == null) {

            throw new IllegalStateException(
                    "Risk assessment severity cannot be null"
            );
        }

        return switch (
                assessment.getSeverity()
                ) {

            case LOW ->
                    IncidentSeverity.LOW;

            case MEDIUM ->
                    IncidentSeverity.MEDIUM;

            case HIGH ->
                    IncidentSeverity.HIGH;

            case CRITICAL ->
                    IncidentSeverity.CRITICAL;
        };
    }


    // =========================================================
    // INCIDENT STATUS TRANSITION VALIDATION
    // =========================================================

    private void validateTransition(
            IncidentStatus current,
            IncidentStatus next
    ) {

        if (current == null) {

            throw new IllegalStateException(
                    "Current incident status cannot be null"
            );
        }

        if (next == null) {

            throw new IllegalArgumentException(
                    "New incident status cannot be null"
            );
        }

        /*
         * Same status is not a transition.
         */
        if (current == next) {

            throw new IllegalStateException(
                    "Incident is already in "
                            + current
            );
        }

        boolean valid =
                switch (current) {

                    /*
                     * OPEN
                     *   ↓
                     * INVESTIGATING
                     */
                    case OPEN ->
                            next
                                    == IncidentStatus.INVESTIGATING;

                    /*
                     * INVESTIGATING
                     *   ↓
                     * RESOLVED
                     */
                    case INVESTIGATING ->
                            next
                                    == IncidentStatus.RESOLVED;

                    /*
                     * RESOLVED
                     *   ↓
                     * CLOSED
                     */
                    case RESOLVED ->
                            next
                                    == IncidentStatus.CLOSED;

                    /*
                     * CLOSED is terminal.
                     */
                    case CLOSED ->
                            false;
                };

        if (!valid) {

            throw new IllegalStateException(
                    "Invalid incident status transition: "
                            + current
                            + " -> "
                            + next
            );
        }
    }
}