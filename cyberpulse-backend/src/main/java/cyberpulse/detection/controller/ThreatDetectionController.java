package cyberpulse.detection.controller;

import cyberpulse.detection.dto.ThreatDetectionResponse;
import cyberpulse.detection.service.ThreatDetectionService;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detections")
public class ThreatDetectionController {

    private final ThreatDetectionService
            detectionService;

    public ThreatDetectionController(
            ThreatDetectionService detectionService
    ) {
        this.detectionService =
                detectionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public ResponseEntity<List<ThreatDetectionResponse>>
    getAll() {

        return ResponseEntity.ok(
                detectionService.getAll()
        );
    }
}