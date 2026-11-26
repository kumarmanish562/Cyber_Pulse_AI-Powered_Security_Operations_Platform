package cyberpulse.apikey.controller;

import cyberpulse.apikey.dto.ApiKeyResponse;
import cyberpulse.apikey.dto.CreateApiKeyRequest;
import cyberpulse.apikey.dto.CreateApiKeyResponse;
import cyberpulse.apikey.service.ServiceApiKeyService;
import cyberpulse.common.security.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/api-keys")
@RequiredArgsConstructor
public class ServiceApiKeyController {

    private final ServiceApiKeyService apiKeyService;
    private final CurrentUserService currentUserService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<CreateApiKeyResponse> create(@Valid @RequestBody CreateApiKeyRequest request) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.createKey(request, currentUserId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public List<ApiKeyResponse> list() {
        return apiKeyService.listKeys();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<Void> revoke(@PathVariable UUID id) {
        apiKeyService.revokeKey(id);
        return ResponseEntity.noContent().build();
    }
}
