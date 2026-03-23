package com.pmp.controller;

import com.pmp.dto.ExecuteRequest;
import com.pmp.dto.FeedbackRequest;
import com.pmp.dto.FeedbackResponse;
import com.pmp.dto.TestResultResponse;
import com.pmp.service.FeedbackService;
import com.pmp.service.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlaygroundController {

    private final TestService testService;
    private final FeedbackService feedbackService;

    @PostMapping("/playgrounds/execute")
    public TestResultResponse executePrompt(@Valid @RequestBody ExecuteRequest request) {
        return testService.executePrompt(request.getPromptVersionId(), request.getVariables());
    }

    @PostMapping("/prompt-versions/{versionId}/feedback")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResponse submitFeedback(@PathVariable Long versionId, @Valid @RequestBody FeedbackRequest request) {
        return feedbackService.submitFeedback(
                versionId,
                request.getQuality(),
                request.getAccuracy(),
                request.getUsefulness(),
                request.getComments());
    }
}
