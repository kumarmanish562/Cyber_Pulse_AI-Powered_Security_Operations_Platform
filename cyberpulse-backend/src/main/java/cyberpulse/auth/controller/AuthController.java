package cyberpulse.auth.controller;

import cyberpulse.auth.dto.*;
import cyberpulse.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {

        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {

        UserResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid
            @RequestBody
            RefreshTokenRequest request
    ) {

        AuthResponse response =
                authService.refresh(
                        request.refreshToken()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid
            @RequestBody
            LogoutRequest request
    ) {

        authService.logout(
                request.refreshToken()
        );

        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> currentUser(
            Authentication authentication
    ) {

        UserResponse response =
                authService.currentUser(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}