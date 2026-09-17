package cyberpulse.common.controller;

import cyberpulse.common.response.ApiResponse;
import cyberpulse.common.validation.TestRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<String>> health() {

        ApiResponse<String> response = ApiResponse.of(
                "CyberPulse backend is running"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TestRequest> test(
            @Valid @RequestBody TestRequest request) {

        return ResponseEntity.ok(request);
    }
}