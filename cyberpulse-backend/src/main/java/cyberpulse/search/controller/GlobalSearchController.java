package cyberpulse.search.controller;

import cyberpulse.search.dto.GlobalSearchResponse;
import cyberpulse.search.service.GlobalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class GlobalSearchController {

    private final GlobalSearchService searchService;

    @GetMapping
    public GlobalSearchResponse search(@RequestParam(defaultValue = "") String q) {
        return searchService.search(q);
    }
}
