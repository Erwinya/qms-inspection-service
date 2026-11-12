package com.halukkilincer.inspection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateInspectionRequest(
        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        @NotBlank(message = "checklistSummary is required")
        @Size(max = 4000, message = "checklistSummary must be at most 4000 characters")
        String checklistSummary,

        @Size(max = 100, message = "lotNumber must be at most 100 characters")
        String lotNumber,

        @Size(max = 100, message = "partNumber must be at most 100 characters")
        String partNumber,

        @NotBlank(message = "inspector is required")
        @Size(max = 120, message = "inspector must be at most 120 characters")
        String inspector
) {
}
