package cyberpulse.intel.service;

import cyberpulse.blocklist.entity.BlockedIp;
import cyberpulse.blocklist.repository.BlockedIpRepository;
import cyberpulse.common.enums.EventType;
import cyberpulse.event.repository.SecurityEventRepository;
import cyberpulse.intel.dto.IpThreatIntelResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class ThreatIntelService {

    private final SecurityEventRepository eventRepository;
    private final BlockedIpRepository blockedIpRepository;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final String abuseIpDbApiKey;

    public ThreatIntelService(
            SecurityEventRepository eventRepository,
            BlockedIpRepository blockedIpRepository,
            ObjectMapper objectMapper,
            @Value("${cyberpulse.intel.abuseipdb-api-key:}") String abuseIpDbApiKey
    ) {
        this.eventRepository = eventRepository;
        this.blockedIpRepository = blockedIpRepository;
        this.objectMapper = objectMapper;
        this.abuseIpDbApiKey = abuseIpDbApiKey != null ? abuseIpDbApiKey.trim() : "";

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3_000);
        factory.setReadTimeout(5_000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    @Transactional(readOnly = true)
    public IpThreatIntelResponse lookupIp(String rawIp) {
        String ip = rawIp != null ? rawIp.trim() : "";
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address: " + ip);
        }

        long totalEvents = eventRepository.countBySourceIp(inetAddress);
        long failedLogins = eventRepository.countBySourceIpAndEventType(inetAddress, EventType.LOGIN_FAILED);
        long portScans = eventRepository.countBySourceIpAndEventType(inetAddress, EventType.PORT_SCAN);

        Optional<BlockedIp> blocked = blockedIpRepository.findByIpAddress(ip)
                .filter(BlockedIp::isActive);

        Integer externalScore = null;
        String externalProvider = "INTERNAL_TELEMETRY";
        if (!abuseIpDbApiKey.isEmpty()) {
            try {
                String body = restClient.get()
                        .uri("https://api.abuseipdb.com/api/v2/check?ipAddress=" + ip + "&maxAgeInDays=90")
                        .header("Key", abuseIpDbApiKey)
                        .header("Accept", "application/json")
                        .retrieve()
                        .body(String.class);
                JsonNode root = objectMapper.readTree(body);
                externalScore = root.path("data").path("abuseConfidenceScore").asInt();
                externalProvider = "ABUSEIPDB";
            } catch (Exception ex) {
                log.warn("AbuseIPDB lookup failed for ip={}: {}", ip, ex.getMessage());
            }
        }

        String verdict = computeVerdict(blocked.isPresent(), failedLogins, portScans, externalScore);

        return new IpThreatIntelResponse(
                ip,
                totalEvents,
                failedLogins,
                portScans,
                blocked.isPresent(),
                blocked.map(BlockedIp::getReason).orElse(null),
                externalScore,
                externalProvider,
                verdict,
                Instant.now()
        );
    }

    private String computeVerdict(boolean isBlocked, long failedLogins, long portScans, Integer externalScore) {
        if (isBlocked || (externalScore != null && externalScore >= 75) || portScans >= 5 || failedLogins >= 10) {
            return "MALICIOUS";
        }
        if ((externalScore != null && externalScore >= 25) || portScans > 0 || failedLogins >= 3) {
            return "SUSPICIOUS";
        }
        return "CLEAN";
    }
}
