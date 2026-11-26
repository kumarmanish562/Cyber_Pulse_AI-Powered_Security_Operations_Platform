package cyberpulse.intel.controller;

import cyberpulse.intel.dto.IpThreatIntelResponse;
import cyberpulse.intel.service.ThreatIntelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/threat-intel")
@RequiredArgsConstructor
public class ThreatIntelController {

    private final ThreatIntelService threatIntelService;

    @GetMapping("/ip/{ip}")
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public IpThreatIntelResponse lookupIp(@PathVariable String ip) {
        return threatIntelService.lookupIp(ip);
    }
}
