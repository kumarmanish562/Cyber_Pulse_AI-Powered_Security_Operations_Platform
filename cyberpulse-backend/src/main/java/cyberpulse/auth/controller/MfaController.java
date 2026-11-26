package cyberpulse.auth.controller;

import cyberpulse.auth.dto.MfaCodeRequest;
import cyberpulse.auth.dto.MfaSetupResponse;
import cyberpulse.auth.service.TotpService;
import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/mfa")
@RequiredArgsConstructor
public class MfaController {

    private final UserRepository userRepository;
    private final TotpService totpService;

    @PostMapping("/setup")
    @Transactional
    public MfaSetupResponse setup(Authentication authentication) {
        User user = userRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String secret = totpService.generateSecret();
        user.setTotpSecret(secret);
        user.setTotpEnabled(false);
        userRepository.save(user);
        return new MfaSetupResponse(secret, totpService.buildOtpAuthUri(user.getUsername(), secret));
    }

    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<Map<String, Object>> verify(
            Authentication authentication,
            @Valid @RequestBody MfaCodeRequest request
    ) {
        User user = userRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!totpService.verifyCode(user.getTotpSecret(), request.code())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        user.setTotpEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("mfaEnabled", true));
    }

    @PostMapping("/disable")
    @Transactional
    public ResponseEntity<Map<String, Object>> disable(
            Authentication authentication,
            @Valid @RequestBody MfaCodeRequest request
    ) {
        User user = userRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!totpService.verifyCode(user.getTotpSecret(), request.code())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        user.setTotpEnabled(false);
        user.setTotpSecret(null);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("mfaEnabled", false));
    }
}
