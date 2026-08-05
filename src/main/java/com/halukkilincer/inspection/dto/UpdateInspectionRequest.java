package com.halukkilincer.inspection.dto;

import com.halukkilincer.inspection.domain.InspectionResult;
import jakarta.validation.constraints.Size;

public record UpdateInspectionRequest(
        @Size(max = 200) String title,
        @Size(max = 4000) String checklistSummary,
        @Size(max = 100) String lotNumber,
        @Size(max = 100) String partNumber,
        @Size(max = 2000) String notes,
        InspectionResult result,
        @Size(max = 32) String relatedNcrNumber
) {
}
