package cyberpulse.audit.service;

import cyberpulse.audit.dto.AuditLogResponse;
import cyberpulse.audit.entity.AuditLog;
import cyberpulse.audit.event.AuditAction;
import cyberpulse.audit.mapper.AuditLogMapper;
import cyberpulse.audit.repository.AuditLogRepository;
import cyberpulse.audit.util.AuditDetailsSerializer;
import cyberpulse.audit.util.AuditRequestContext;
import cyberpulse.common.security.CurrentUserService;
import cyberpulse.user.entity.User;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    private final AuditLogMapper auditLogMapper;

    private final AuditRequestContext auditRequestContext;

    private final CurrentUserService currentUserService;

    private final AuditDetailsSerializer auditDetailsSerializer;


    // =========================================================
    // RECORD AUDIT
    // =========================================================

    @Transactional
    public void record(
            AuditAction action,
            String entityType,
            UUID entityId,
            Object details
    ) {

        AuditLog auditLog =
                new AuditLog();


        User currentUser =
                currentUserService
                        .getCurrentUser()
                        .orElse(null);


        auditLog.setUser(
                currentUser
        );


        auditLog.setAction(
                action.name()
        );


        auditLog.setEntityType(
                entityType
        );


        auditLog.setEntityId(
                entityId
        );


        auditLog.setIpAddress(
                auditRequestContext
                        .getIpAddress()
        );


        auditLog.setUserAgent(
                auditRequestContext
                        .getUserAgent()
        );


        auditLog.setDetails(
                details == null
                        ? null
                        : auditDetailsSerializer
                        .serialize(details)
        );


        auditLogRepository.save(
                auditLog
        );
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findAll(
            Pageable pageable
    ) {

        return auditLogRepository
                .findAllByOrderByCreatedAtDesc(
                        pageable
                )
                .map(
                        auditLogMapper::toResponse
                );
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public AuditLogResponse findById(
            UUID id
    ) {

        AuditLog auditLog =
                auditLogRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Audit log not found: "
                                                + id
                                )
                        );


        return auditLogMapper.toResponse(
                auditLog
        );
    }


    // =========================================================
    // GET BY USER
    // =========================================================

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findByUser(
            UUID userId,
            Pageable pageable
    ) {

        return auditLogRepository
                .findByUser_IdOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(
                        auditLogMapper::toResponse
                );
    }


    // =========================================================
    // GET BY ACTION
    // =========================================================

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findByAction(
            AuditAction action,
            Pageable pageable
    ) {

        return auditLogRepository
                .findByActionOrderByCreatedAtDesc(
                        action.name(),
                        pageable
                )
                .map(
                        auditLogMapper::toResponse
                );
    }


    // =========================================================
    // GET BY ENTITY
    // =========================================================

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findByEntity(
            String entityType,
            UUID entityId,
            Pageable pageable
    ) {

        return auditLogRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                        entityType,
                        entityId,
                        pageable
                )
                .map(
                        auditLogMapper::toResponse
                );
    }
}