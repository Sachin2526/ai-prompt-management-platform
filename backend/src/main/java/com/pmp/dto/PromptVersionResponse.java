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
public class PromptVersionResponse {
    private Long id;
    private Long promptId;
    private String promptText;
    private Integer versionNumber;
    private LocalDateTime createdAt;
}
