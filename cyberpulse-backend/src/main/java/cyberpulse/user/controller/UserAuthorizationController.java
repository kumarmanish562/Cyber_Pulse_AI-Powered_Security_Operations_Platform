package cyberpulse.user.controller;

import cyberpulse.user.dto.AssignRoleRequest;
import cyberpulse.user.service.UserAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserAuthorizationController {

    private final UserAuthorizationService
            authorizationService;

    public UserAuthorizationController(
            UserAuthorizationService authorizationService
    ) {
        this.authorizationService =
                authorizationService;
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<Void> assignRole(
            @PathVariable UUID userId,
            @Valid @RequestBody AssignRoleRequest request
    ) {

        authorizationService.assignRole(
                userId,
                request.role()
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasAuthority('ROLE_REMOVE')")
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID userId,
            @PathVariable String roleName
    ) {

        authorizationService.removeRole(
                userId,
                roleName
        );

        return ResponseEntity.noContent().build();
    }
}