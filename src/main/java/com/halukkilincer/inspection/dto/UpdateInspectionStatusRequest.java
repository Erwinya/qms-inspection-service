package com.halukkilincer.inspection.dto;

import com.halukkilincer.inspection.domain.InspectionResult;
import com.halukkilincer.inspection.domain.InspectionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateInspectionStatusRequest(
        @NotNull(message = "status is required")
        InspectionStatus status,

        InspectionResult result,

        @Size(max = 2000, message = "note must be at most 2000 characters")
        String note,

        @Size(max = 32, message = "relatedNcrNumber must be at most 32 characters")
        String relatedNcrNumber
) {
}
