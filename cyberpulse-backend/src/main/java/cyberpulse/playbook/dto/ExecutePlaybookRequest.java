package cyberpulse.playbook.dto;

import jakarta.validation.constraints.NotNull;

public record ExecutePlaybookRequest(
        @NotNull(message = "Playbook action is required")
        PlaybookAction action
) {
    public enum PlaybookAction {
        CONTAIN_AND_BLOCK_IP,
        ESCALATE_ALERT,
        CLOSE_FALSE_POSITIVE
    }
}
