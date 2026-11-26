package cyberpulse.lifecycle;

import cyberpulse.auth.repository.PasswordResetTokenRepository;
import cyberpulse.auth.repository.RefreshTokenRepository;
import cyberpulse.event.repository.SecurityEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataRetentionServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private SecurityEventRepository securityEventRepository;
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldPurgeExpiredTokensAndOldEventsWhenLockAcquired() {
        when(jdbcTemplate.queryForObject(eq("SELECT pg_try_advisory_lock(?)"), eq(Boolean.class), any(Long.class)))
                .thenReturn(true);
        when(refreshTokenRepository.deleteByExpiresAtBefore(any(Instant.class))).thenReturn(3L);
        when(passwordResetTokenRepository.deleteByExpiresAtBefore(any(Instant.class))).thenReturn(2L);
        when(securityEventRepository.deleteByEventTimeBefore(any(Instant.class))).thenReturn(10L);

        DataRetentionService service = new DataRetentionService(
                refreshTokenRepository,
                passwordResetTokenRepository,
                securityEventRepository,
                jdbcTemplate,
                true,
                90
        );

        service.runCleanup();

        verify(refreshTokenRepository).deleteByExpiresAtBefore(any(Instant.class));
        verify(passwordResetTokenRepository).deleteByExpiresAtBefore(any(Instant.class));
        verify(securityEventRepository).deleteByEventTimeBefore(any(Instant.class));
        verify(jdbcTemplate).queryForObject(eq("SELECT pg_advisory_unlock(?)"), eq(Boolean.class), any(Long.class));
    }

    @Test
    void shouldSkipCleanupWhenAnotherInstanceHoldsLock() {
        when(jdbcTemplate.queryForObject(eq("SELECT pg_try_advisory_lock(?)"), eq(Boolean.class), any(Long.class)))
                .thenReturn(false);

        DataRetentionService service = new DataRetentionService(
                refreshTokenRepository,
                passwordResetTokenRepository,
                securityEventRepository,
                jdbcTemplate,
                true,
                90
        );

        service.runCleanup();

        verifyNoInteractions(refreshTokenRepository, passwordResetTokenRepository, securityEventRepository);
    }
}
