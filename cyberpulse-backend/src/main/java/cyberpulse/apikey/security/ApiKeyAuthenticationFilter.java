package cyberpulse.apikey.security;

import cyberpulse.apikey.service.ServiceApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Authenticates automated log shippers (FluentBit, Filebeat, SIEM forwarders)
 * presenting a valid X-API-Key header.
 */
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "X-API-Key";
    private final ServiceApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String rawKey = request.getHeader(API_KEY_HEADER);
            if (rawKey != null && !rawKey.isBlank()) {
                apiKeyService.authenticate(rawKey).ifPresent(apiKey -> {
                    List<SimpleGrantedAuthority> authorities = Arrays.stream(apiKey.getScopes().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            "apikey:" + apiKey.getName(),
                            null,
                            authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            }
        }

        filterChain.doFilter(request, response);
    }
}
