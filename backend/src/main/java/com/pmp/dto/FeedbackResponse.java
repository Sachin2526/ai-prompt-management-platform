package com.pmp.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {
    private Long id;
    private Long promptVersionId;
    private Integer quality;
    private Integer accuracy;
    private Integer usefulness;
    private String comments;
    private LocalDateTime createdAt;
}
