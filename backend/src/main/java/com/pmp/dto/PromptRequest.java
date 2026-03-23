package com.pmp.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class PromptRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    private String description;

    @NotBlank(message = "Author is required")
    @Size(max = 100)
    private String author;

    private List<String> tags;

    @NotBlank(message = "Initial prompt text cannot be empty")
    private String initialPromptText;
}
