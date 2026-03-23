package com.pmp.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptResponse {
    private Long id;
    private String title;
    private String description;
    private String author;
    private List<String> tags;
    private LocalDateTime createdAt;
}
