package cyberpulse.report.controller;

import cyberpulse.report.dto.ShiftHandoverReport;
import cyberpulse.report.service.ShiftHandoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ShiftHandoverService shiftHandoverService;

    @GetMapping("/shift-handover")
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public ShiftHandoverReport getShiftHandover() {
        return shiftHandoverService.generate24HourReport();
    }

    @GetMapping(value = "/shift-handover/download", produces = MediaType.TEXT_MARKDOWN_VALUE)
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public ResponseEntity<String> downloadMarkdown() {
        ShiftHandoverReport report = shiftHandoverService.generate24HourReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"soc-shift-handover.md\"")
                .contentType(MediaType.parseMediaType("text/markdown"))
                .body(report.markdownSummary());
    }
}
