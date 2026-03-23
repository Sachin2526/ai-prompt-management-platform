package com.pmp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackStats {
    private Double averageQuality;
    private Double averageAccuracy;
    private Double averageUsefulness;
    private Long totalReviews;
}
