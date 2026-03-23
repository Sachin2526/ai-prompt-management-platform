package com.pmp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class ExecuteRequest {
    @NotNull(message = "Prompt version ID is required")
    private Long promptVersionId;
    
    private Map<String, Object> variables;
}
