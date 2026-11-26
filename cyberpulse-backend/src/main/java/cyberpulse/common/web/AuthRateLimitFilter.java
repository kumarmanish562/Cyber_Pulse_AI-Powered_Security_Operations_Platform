package cyberpulse.common.web;

import cyberpulse.common.response.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Protects public authentication endpoints against brute-force and credential-stuffing attacks
 * using a bounded, per-IP fixed-window rate limiter.
 */
@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_TRACKED_CLIENTS = 10_000;

    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh"
    );

    private final ObjectMapper objectMapper;
    private final boolean enabled;
    private final int maxRequests;
    private final long windowMillis;
    private final Clock clock;
    private final ConcurrentHashMap<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public AuthRateLimitFilter(
            ObjectMapper objectMapper,
            @Value("${cyberpulse.rate-limit.enabled:true}") boolean enabled,
            @Value("${cyberpulse.rate-limit.auth.max-requests:20}") int maxRequests,
            @Value("${cyberpulse.rate-limit.auth.window-seconds:60}") long windowSeconds
    ) {
        this(objectMapper, enabled, maxRequests, windowSeconds, Clock.systemUTC());
    }

    AuthRateLimitFilter(
            ObjectMapper objectMapper,
            boolean enabled,
            int maxRequests,
            long windowSeconds,
            Clock clock
    ) {
        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.maxRequests = Math.max(1, maxRequests);
        this.windowMillis = Math.max(1L, windowSeconds) * 1000L;
        this.clock = clock;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!enabled) {
            return true;
        }
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return true;
        }
        return !PROTECTED_PATHS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String clientKey = resolveClientIp(request) + ":" + request.getRequestURI();
        long now = clock.millis();

        if (counters.size() > MAX_TRACKED_CLIENTS) {
            evictExpired(now);
        }

        WindowCounter counter = counters.compute(clientKey, (key, existing) -> {
            if (existing == null || now - existing.windowStartMillis >= windowMillis) {
                return new WindowCounter(now, 1);
            }
            return new WindowCounter(existing.windowStartMillis, existing.count + 1);
        });

        if (counter.count > maxRequests) {
            long retryAfterSeconds = Math.max(
                    1L,
                    (counter.windowStartMillis + windowMillis - now + 999L) / 1000L
            );
            writeRateLimitResponse(request, response, retryAfterSeconds);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void evictExpired(long now) {
        counters.entrySet().removeIf(entry -> now - entry.getValue().windowStartMillis >= windowMillis);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            int commaIdx = forwardedFor.indexOf(',');
            String firstIp = (commaIdx > -1 ? forwardedFor.substring(0, commaIdx) : forwardedFor).trim();
            if (!firstIp.isEmpty()) {
                return firstIp;
            }
        }
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr != null ? remoteAddr : "unknown";
    }

    private void writeRateLimitResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            long retryAfterSeconds
    ) throws IOException {

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));

        ApiErrorResponse error = new ApiErrorResponse(
                Instant.now(clock),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "RATE_LIMIT_EXCEEDED",
                "Too many authentication requests. Please try again later.",
                request.getRequestURI(),
                MDC.get("correlationId"),
                List.of()
        );

        objectMapper.writeValue(response.getOutputStream(), error);
    }

    private record WindowCounter(long windowStartMillis, int count) {
    }
}
