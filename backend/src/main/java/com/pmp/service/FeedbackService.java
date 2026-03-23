package com.pmp.service;

import com.pmp.model.Feedback;
import com.pmp.model.PromptVersion;
import com.pmp.repository.FeedbackRepository;
import com.pmp.dto.FeedbackStats;
import com.pmp.dto.FeedbackResponse;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final PromptService promptService;

    public FeedbackResponse submitFeedback(Long versionId, Integer quality, Integer accuracy, Integer usefulness, String comments) {
        PromptVersion version = promptService.getEntityVersion(versionId);
        
        Feedback feedback = new Feedback();
        feedback.setPromptVersion(version);
        feedback.setRatingQuality(quality);
        feedback.setRatingAccuracy(accuracy);
        feedback.setRatingUsefulness(usefulness);
        feedback.setComments(comments);

        return toResponse(feedbackRepository.save(feedback));
    }

    public FeedbackStats getStats(Long versionId) {
        FeedbackStats stats = feedbackRepository.getStatsForVersion(versionId);
        if (stats == null || stats.getTotalReviews() == 0) {
            return new FeedbackStats(0.0, 0.0, 0.0, 0L);
        }
        return stats;
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .promptVersionId(feedback.getPromptVersion().getId())
                .quality(feedback.getRatingQuality())
                .accuracy(feedback.getRatingAccuracy())
                .usefulness(feedback.getRatingUsefulness())
                .comments(feedback.getComments())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
