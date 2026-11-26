package cyberpulse.soc.controller;

import cyberpulse.soc.dto.SocPostureResponse;
import cyberpulse.soc.service.SocPostureService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/soc")
@RequiredArgsConstructor
public class SocPostureController {

    private final SocPostureService socPostureService;

    @GetMapping("/posture")
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public SocPostureResponse getPosture() {
        return socPostureService.getPosture();
    }
}
