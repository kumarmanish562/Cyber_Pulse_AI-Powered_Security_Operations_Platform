package cyberpulse.audit.repository;

import cyberpulse.audit.entity.AuditLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );

    Page<AuditLog> findByUser_IdOrderByCreatedAtDesc(
            UUID userId,
            Pageable pageable
    );

    Page<AuditLog> findByActionOrderByCreatedAtDesc(
            String action,
            Pageable pageable
    );

    Page<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType,
            UUID entityId,
            Pageable pageable
    );
}