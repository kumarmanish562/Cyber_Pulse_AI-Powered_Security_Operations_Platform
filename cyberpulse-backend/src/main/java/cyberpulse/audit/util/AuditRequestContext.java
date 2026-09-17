package cyberpulse.audit.util;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@RequiredArgsConstructor
public class AuditRequestContext {

    private final HttpServletRequest request;

    public InetAddress getIpAddress() {

        String forwardedFor =
                request.getHeader("X-Forwarded-For");

        String ipAddress;

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            ipAddress =
                    forwardedFor
                            .split(",")[0]
                            .trim();

        } else {

            ipAddress =
                    request.getRemoteAddr();
        }

        if (ipAddress == null
                || ipAddress.isBlank()) {

            return null;
        }

        try {

            return InetAddress.getByName(
                    ipAddress
            );

        } catch (UnknownHostException exception) {

            return null;
        }
    }

    public String getUserAgent() {

        String userAgent =
                request.getHeader("User-Agent");

        if (userAgent == null) {
            return null;
        }

        return userAgent.length() > 1000
                ? userAgent.substring(0, 1000)
                : userAgent;
    }
}