package cyberpulse.event.repository;

import cyberpulse.common.enums.Severity;
import cyberpulse.event.entity.SecurityEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.net.InetAddress;
import java.time.Instant;
import java.util.UUID;



public interface SecurityEventRepository
        extends JpaRepository<SecurityEvent, UUID>,
        JpaSpecificationExecutor<SecurityEvent> {

    Page<SecurityEvent> findBySeverity(
            Severity severity,
            Pageable pageable
    );

    Page<SecurityEvent> findByEventType(
            String eventType,
            Pageable pageable
    );

    long countBySourceIpAndEventTypeAndEventTimeBetween(
            String sourceIp,
            String eventType,
            Instant start,
            Instant end
    );

    @Query("""
        SELECT COUNT(e)
        FROM SecurityEvent e
        WHERE e.eventType =
            cyberpulse.common.enums.EventType.LOGIN_FAILED
          AND e.sourceIp = :sourceIp
          AND e.eventTime >= :from
          AND e.eventTime <= :to
    """)
    long countFailedLoginsFromIp(
            @Param("sourceIp")
            InetAddress sourceIp,

            @Param("from")
            Instant from,

            @Param("to")
            Instant to
    );
}
