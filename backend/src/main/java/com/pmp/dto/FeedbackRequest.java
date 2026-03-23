package com.pmp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedbackRequest {
    @NotNull(message = "Quality rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be exactly 1 to 5")
    private Integer quality;

    @NotNull(message = "Accuracy rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be exactly 1 to 5")
    private Integer accuracy;

    @NotNull(message = "Usefulness rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be exactly 1 to 5")
    private Integer usefulness;

    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    private String comments;
}
