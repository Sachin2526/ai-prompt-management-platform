package com.pmp.service;

import com.pmp.dto.TestResultResponse;
import com.pmp.model.PromptVersion;
import com.pmp.model.TestResult;
import com.pmp.repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    private final TestResultRepository testResultRepository;
    private final PromptService promptService;
    private final AiService aiService;

    public TestResultResponse executePrompt(Long versionId, Map<String, Object> variables) {
        PromptVersion version = promptService.getEntityVersion(versionId);

        String promptText = version.getPromptText();
        log.debug("[RENDER] Original template: {}", promptText);
        log.debug("[RENDER] Variables received: {}", variables);

        if (variables != null && !variables.isEmpty()) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? String.valueOf(entry.getValue()) : "";
                promptText = promptText.replace(placeholder, value);
            }
        }

        log.info("[RENDER] Final prompt sent to AI: {}", promptText);

        try {
            String responseContent = aiService.runPrompt(promptText);

            TestResult result = new TestResult();
            result.setPromptVersion(version);
            result.setInputs(variables);
            result.setOutputs(responseContent);

            return toResponse(testResultRepository.save(result));
        } catch (Exception e) {
            throw new RuntimeException("AI execution failed: " + e.getMessage(), e);
        }
    }

    public List<TestResultResponse> getTestHistory(Long versionId) {
        return testResultRepository.findByPromptVersionIdOrderByExecutedAtDesc(versionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TestResultResponse toResponse(TestResult result) {
        return TestResultResponse.builder()
                .id(result.getId())
                .promptVersionId(result.getPromptVersion().getId())
                .inputs(result.getInputs())
                .outputs(result.getOutputs())
                .executedAt(result.getExecutedAt())
                .build();
    }
}