package cyberpulse.blocklist.controller;

import cyberpulse.blocklist.dto.BlockIpRequest;
import cyberpulse.blocklist.dto.BlockedIpResponse;
import cyberpulse.blocklist.service.BlocklistService;
import cyberpulse.common.security.CurrentUserService;
import cyberpulse.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/blocklist")
@RequiredArgsConstructor
public class BlocklistController {

    private final BlocklistService blocklistService;
    private final CurrentUserService currentUserService;

    @GetMapping
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public List<BlockedIpResponse> listActive() {
        return blocklistService.getActiveBlocklist();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INCIDENT_UPDATE')")
    public ResponseEntity<BlockedIpResponse> blockIp(@Valid @RequestBody BlockIpRequest request) {
        UUID userId = currentUserService.getCurrentUser().map(User::getId).orElse(null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(blocklistService.blockIp(request, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INCIDENT_UPDATE')")
    public ResponseEntity<Void> unblockIp(@PathVariable UUID id) {
        blocklistService.unblockIp(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/feed", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public ResponseEntity<String> exportFirewallFeed() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cyberpulse-blocklist.txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(blocklistService.exportPlaintextFeed());
    }
}
