package com.halukkilincer.inspection.dto;

import com.halukkilincer.inspection.domain.InspectionResult;
import jakarta.validation.constraints.Size;

public record UpdateInspectionRequest(
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        @Size(max = 4000, message = "checklistSummary must be at most 4000 characters")
        String checklistSummary,

        @Size(max = 100, message = "lotNumber must be at most 100 characters")
        String lotNumber,

        @Size(max = 100, message = "partNumber must be at most 100 characters")
        String partNumber,

        @Size(max = 2000, message = "notes must be at most 2000 characters")
        String notes,

        InspectionResult result,

        @Size(max = 32, message = "relatedNcrNumber must be at most 32 characters")
        String relatedNcrNumber
) {
}
