package com.pmp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompareResponse {
    private PromptVersionResponse versionA;
    private PromptVersionResponse versionB;
    
    /**
     * A structured summary of what text was added, removed, or changed.
     */
    private String differencesSummary;
    
    /**
     * Complete textual diff 
     */
    private String detailedDiff;
}
