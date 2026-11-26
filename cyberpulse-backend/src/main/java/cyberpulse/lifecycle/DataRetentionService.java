package cyberpulse.lifecycle;

import cyberpulse.auth.repository.PasswordResetTokenRepository;
import cyberpulse.auth.repository.RefreshTokenRepository;
import cyberpulse.event.repository.SecurityEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled data lifecycle job that purges expired auth tokens and prunes
 * aged security events, guarded by a PostgreSQL advisory lock for multi-instance safety.
 */
@Service
@Slf4j
public class DataRetentionService {

    private static final long ADVISORY_LOCK_ID = 88421001L;

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final SecurityEventRepository securityEventRepository;
    private final JdbcTemplate jdbcTemplate;
    private final boolean enabled;
    private final int securityEventsRetentionDays;

    public DataRetentionService(
            RefreshTokenRepository refreshTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            SecurityEventRepository securityEventRepository,
            JdbcTemplate jdbcTemplate,
            @Value("${cyberpulse.retention.enabled:true}") boolean enabled,
            @Value("${cyberpulse.retention.security-events-days:90}") int securityEventsRetentionDays
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.securityEventRepository = securityEventRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.enabled = enabled;
        this.securityEventsRetentionDays = Math.max(1, securityEventsRetentionDays);
    }

    @Scheduled(cron = "${cyberpulse.retention.cron:0 0 2 * * *}")
    @Transactional
    public void runCleanup() {
        if (!enabled) {
            return;
        }
        if (!tryAcquireLock()) {
            log.debug("Skipping retention cleanup; another instance holds the advisory lock");
            return;
        }

        try {
            Instant now = Instant.now();
            long expiredRefresh = refreshTokenRepository.deleteByExpiresAtBefore(now);
            long expiredResets = passwordResetTokenRepository.deleteByExpiresAtBefore(now);

            Instant eventCutoff = now.minus(securityEventsRetentionDays, ChronoUnit.DAYS);
            long prunedEvents = securityEventRepository.deleteByEventTimeBefore(eventCutoff);

            log.info("Data retention cleanup complete: refreshTokens={}, resetTokens={}, securityEvents={}",
                    expiredRefresh, expiredResets, prunedEvents);
        } finally {
            releaseLock();
        }
    }

    private boolean tryAcquireLock() {
        try {
            Boolean acquired = jdbcTemplate.queryForObject(
                    "SELECT pg_try_advisory_lock(?)",
                    Boolean.class,
                    ADVISORY_LOCK_ID
            );
            return Boolean.TRUE.equals(acquired);
        } catch (Exception ex) {
            log.debug("Advisory lock check skipped: {}", ex.getMessage());
            return true;
        }
    }

    private void releaseLock() {
        try {
            jdbcTemplate.queryForObject(
                    "SELECT pg_advisory_unlock(?)",
                    Boolean.class,
                    ADVISORY_LOCK_ID
            );
        } catch (Exception ex) {
            log.debug("Advisory unlock skipped: {}", ex.getMessage());
        }
    }
}
