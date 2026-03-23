package com.pmp.controller;

import com.pmp.dto.PromptRequest;
import com.pmp.dto.PromptResponse;
import com.pmp.dto.PromptVersionResponse;
import com.pmp.dto.TestResultResponse;
import com.pmp.dto.FeedbackStats;
import com.pmp.dto.CompareResponse;
import com.pmp.service.PromptService;
import com.pmp.service.TestService;
import com.pmp.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PromptController {

    private final PromptService promptService;
    private final TestService testService;
    private final FeedbackService feedbackService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromptResponse createPrompt(@Valid @RequestBody PromptRequest request) {
        return promptService.createPrompt(request);
    }

    @GetMapping
    public Page<PromptResponse> getPrompts(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "tag", required = false) String tag,
            Pageable pageable) {

        query = (query == null) ? "" : query.trim().toLowerCase();
        title = (title == null) ? "" : title.trim().toLowerCase();
        author = (author == null) ? "" : author.trim().toLowerCase();
        tag = (tag == null) ? "" : tag.trim().toLowerCase();

        if (!title.isEmpty() || !author.isEmpty() || !tag.isEmpty()) {
            return promptService.searchPromptsRefined(title, author, tag, pageable);
        }

        if (!query.isEmpty()) {
            return promptService.searchPrompts(query, pageable);
        }

        return promptService.getAllPrompts(pageable);
    }

    @GetMapping("/{id}")
    public PromptResponse getPrompt(@PathVariable("id") Long id) {
        return promptService.getPrompt(id);
    }

    @PutMapping("/{id}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public PromptVersionResponse createNewVersion(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return promptService.createNewVersion(id, payload.get("promptText"));
    }

    @GetMapping("/{id}/versions")
    public List<PromptVersionResponse> getVersions(@PathVariable Long id) {
        return promptService.getVersionsForPrompt(id);
    }

    @GetMapping("/{id}/compare")
    public CompareResponse compareVersions(
            @PathVariable("id") Long id,
            @RequestParam(name = "versionA") Integer versionA,
            @RequestParam(name = "versionB") Integer versionB) {
        return promptService.compareVersions(id, versionA, versionB);
    }

    @GetMapping("/versions/{versionId}/tests")
    public List<TestResultResponse> getTestHistory(@PathVariable("versionId") Long versionId) {
        return testService.getTestHistory(versionId);
    }

    @GetMapping("/versions/{versionId}/feedback/stats")
    public FeedbackStats getFeedbackStats(@PathVariable("versionId") Long versionId) {
        return feedbackService.getStats(versionId);
    }
}
