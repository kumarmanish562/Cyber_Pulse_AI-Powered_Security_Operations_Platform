package cyberpulse.simulator.controller;

import cyberpulse.simulator.dto.SimulationRequest;
import cyberpulse.simulator.dto.SimulationResponse;
import cyberpulse.simulator.service.AttackSimulatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulator")
@RequiredArgsConstructor
public class AttackSimulatorController {

    private final AttackSimulatorService simulatorService;

    @PostMapping("/run")
    @PreAuthorize("hasAuthority('EVENT_WRITE')")
    public ResponseEntity<SimulationResponse> run(@Valid @RequestBody SimulationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(simulatorService.runScenario(request.scenario()));
    }
}
