package cyberpulse.common.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class AuthRateLimitFilterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldAllowRequestsUpToLimitAndBlockExcessWith429() throws Exception {
        MutableClock clock = new MutableClock(1_000_000L);
        AuthRateLimitFilter filter = new AuthRateLimitFilter(objectMapper, true, 2, 60, clock);

        for (int i = 0; i < 2; i++) {
            MockHttpServletRequest request = postRequest("/api/v1/auth/login", "10.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain();

            filter.doFilter(request, response, chain);
            assertEquals(HttpStatus.OK.value(), response.getStatus());
            assertNotNull(chain.getRequest());
        }

        MockHttpServletRequest blockedRequest = postRequest("/api/v1/auth/login", "10.0.0.1");
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        MockFilterChain blockedChain = new MockFilterChain();

        filter.doFilter(blockedRequest, blockedResponse, blockedChain);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), blockedResponse.getStatus());
        assertNotNull(blockedResponse.getHeader(HttpHeaders.RETRY_AFTER));
        assertTrue(blockedResponse.getContentAsString().contains("RATE_LIMIT_EXCEEDED"));
        assertNull(blockedChain.getRequest());
    }

    @Test
    void shouldResetCounterAfterWindowExpires() throws Exception {
        MutableClock clock = new MutableClock(1_000_000L);
        AuthRateLimitFilter filter = new AuthRateLimitFilter(objectMapper, true, 1, 60, clock);

        MockHttpServletResponse first = new MockHttpServletResponse();
        filter.doFilter(postRequest("/api/v1/auth/register", "10.0.0.2"), first, new MockFilterChain());
        assertEquals(HttpStatus.OK.value(), first.getStatus());

        MockHttpServletResponse second = new MockHttpServletResponse();
        filter.doFilter(postRequest("/api/v1/auth/register", "10.0.0.2"), second, new MockFilterChain());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), second.getStatus());

        clock.advanceMillis(60_000L);

        MockHttpServletResponse afterWindow = new MockHttpServletResponse();
        filter.doFilter(postRequest("/api/v1/auth/register", "10.0.0.2"), afterWindow, new MockFilterChain());
        assertEquals(HttpStatus.OK.value(), afterWindow.getStatus());
    }

    @Test
    void shouldUseXForwardedForAndIgnoreNonProtectedPaths() throws Exception {
        MutableClock clock = new MutableClock(1_000_000L);
        AuthRateLimitFilter filter = new AuthRateLimitFilter(objectMapper, true, 1, 60, clock);

        MockHttpServletRequest healthRequest = new MockHttpServletRequest("GET", "/api/v1/health");
        MockHttpServletResponse healthResponse = new MockHttpServletResponse();
        filter.doFilter(healthRequest, healthResponse, new MockFilterChain());
        assertEquals(HttpStatus.OK.value(), healthResponse.getStatus());

        MockHttpServletRequest ip1 = postRequest("/api/v1/auth/login", "192.168.1.1");
        ip1.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.1");
        MockHttpServletResponse res1 = new MockHttpServletResponse();
        filter.doFilter(ip1, res1, new MockFilterChain());
        assertEquals(HttpStatus.OK.value(), res1.getStatus());

        MockHttpServletRequest ip2 = postRequest("/api/v1/auth/login", "192.168.1.1");
        ip2.addHeader("X-Forwarded-For", "203.0.113.11, 10.0.0.1");
        MockHttpServletResponse res2 = new MockHttpServletResponse();
        filter.doFilter(ip2, res2, new MockFilterChain());
        assertEquals(HttpStatus.OK.value(), res2.getStatus());
    }

    private MockHttpServletRequest postRequest(String uri, String remoteAddr) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", uri);
        request.setRemoteAddr(remoteAddr);
        return request;
    }

    private static final class MutableClock extends Clock {
        private final AtomicLong millis;

        private MutableClock(long initialMillis) {
            this.millis = new AtomicLong(initialMillis);
        }

        void advanceMillis(long delta) {
            millis.addAndGet(delta);
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(millis.get());
        }
    }
}
