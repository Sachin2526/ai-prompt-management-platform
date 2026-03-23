package com.pmp.service;

import com.pmp.model.Prompt;
import com.pmp.model.PromptVersion;
import com.pmp.repository.PromptRepository;
import com.pmp.repository.PromptVersionRepository;
import com.pmp.dto.PromptRequest;
import com.pmp.dto.PromptResponse;
import com.pmp.dto.PromptVersionResponse;
import com.pmp.dto.CompareResponse;
import com.pmp.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;
    private final PromptVersionRepository promptVersionRepository;

    @Transactional
    public PromptResponse createPrompt(PromptRequest request) {
        Prompt prompt = new Prompt();
        prompt.setTitle(request.getTitle());
        prompt.setDescription(request.getDescription());
        prompt.setAuthor(request.getAuthor());

        // Clean and deduplicate tags
        if (request.getTags() != null) {
            List<String> cleanedTags = request.getTags().stream()
                    .filter(java.util.Objects::nonNull)
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
            prompt.setTags(cleanedTags);
        }

        prompt = promptRepository.save(prompt);

        PromptVersion initialVersion = new PromptVersion();
        initialVersion.setPrompt(prompt);
        initialVersion.setPromptText(request.getInitialPromptText());
        initialVersion.setVersionNumber(1);

        promptVersionRepository.save(initialVersion);
        return toResponse(prompt);
    }

    public Page<PromptResponse> getAllPrompts(Pageable pageable) {
        return promptRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<PromptResponse> searchPrompts(String query, Pageable pageable) {
        String normalizedQuery = (query == null) ? "" : query.trim().toLowerCase();
        return promptRepository.searchPrompts(normalizedQuery, pageable)
                .map(this::toResponse);
    }

    public Page<PromptResponse> searchPromptsRefined(String title, String author, String tag, Pageable pageable) {
        String normalizedTitle = (title == null) ? "" : title.trim().toLowerCase();
        String normalizedAuthor = (author == null) ? "" : author.trim().toLowerCase();
        String normalizedTag = (tag == null) ? "" : tag.trim().toLowerCase();

        return promptRepository.searchPromptsRefined(normalizedTitle, normalizedAuthor, normalizedTag, pageable)
                .map(this::toResponse);
    }

    public PromptResponse getPrompt(Long id) {
        return promptRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Prompt not found with id: " + id));
    }

    @Transactional
    public PromptVersionResponse createNewVersion(Long promptId, String newPromptText) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new ResourceNotFoundException("Prompt not found with id: " + promptId));

        Integer masterVersionNumber = promptVersionRepository
                .findTopByPromptIdOrderByVersionNumberDesc(promptId)
                .map(PromptVersion::getVersionNumber)
                .orElse(0);

        PromptVersion newVersion = new PromptVersion();
        newVersion.setPrompt(prompt);
        newVersion.setPromptText(newPromptText);
        newVersion.setVersionNumber(masterVersionNumber + 1);

        return toVersionResponse(promptVersionRepository.save(newVersion));
    }

    public List<PromptVersionResponse> getVersionsForPrompt(Long promptId) {
        return promptVersionRepository.findByPromptIdOrderByVersionNumberDesc(promptId).stream()
                .map(this::toVersionResponse)
                .collect(Collectors.toList());
    }

    public PromptVersionResponse getVersion(Long versionId) {
        return promptVersionRepository.findById(versionId)
                .map(this::toVersionResponse)
                .orElseThrow(() -> new RuntimeException("Version not found with id: " + versionId));
    }

    public CompareResponse compareVersions(Long promptId, Integer versionA, Integer versionB) {
        PromptVersion vA = promptVersionRepository.findByPromptIdAndVersionNumber(promptId, versionA)
                .orElseThrow(() -> new ResourceNotFoundException("Version " + versionA + " not found"));
        PromptVersion vB = promptVersionRepository.findByPromptIdAndVersionNumber(promptId, versionB)
                .orElseThrow(() -> new ResourceNotFoundException("Version " + versionB + " not found"));

        String textA = vA.getPromptText() != null ? vA.getPromptText() : "";
        String textB = vB.getPromptText() != null ? vB.getPromptText() : "";

        String summary;
        if (textA.equals(textB)) {
            summary = "Texts are identical.";
        } else {
            summary = String.format(
                    "Version %d is %d characters, Version %d is %d characters. Difference length: %d chars.",
                    versionA, textA.length(), versionB, textB.length(), Math.abs(textB.length() - textA.length()));
        }

        return CompareResponse.builder()
                .versionA(toVersionResponse(vA))
                .versionB(toVersionResponse(vB))
                .differencesSummary(summary)
                .detailedDiff(
                        "--- Version " + versionA + " ---\n" + textA + "\n\n--- Version " + versionB + " ---\n" + textB)
                .build();
    }

    private PromptResponse toResponse(Prompt prompt) {
        return PromptResponse.builder()
                .id(prompt.getId())
                .title(prompt.getTitle())
                .description(prompt.getDescription())
                .author(prompt.getAuthor())
                .tags(prompt.getTags())
                .createdAt(prompt.getCreatedAt())
                .build();
    }

    private PromptVersionResponse toVersionResponse(PromptVersion version) {
        return PromptVersionResponse.builder()
                .id(version.getId())
                .promptId(version.getPrompt().getId())
                .promptText(version.getPromptText())
                .versionNumber(version.getVersionNumber())
                .createdAt(version.getCreatedAt())
                .build();
    }

    // Helper for internal service use to avoid recursion in API responses
    public PromptVersion getEntityVersion(Long versionId) {
        return promptVersionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found with id: " + versionId));
    }
}
