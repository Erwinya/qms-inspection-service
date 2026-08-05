package com.halukkilincer.inspection.dto;

import com.halukkilincer.inspection.domain.InspectionResult;
import com.halukkilincer.inspection.domain.InspectionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateInspectionStatusRequest(
        @NotNull InspectionStatus status,
        InspectionResult result,
        @Size(max = 2000) String note,
        @Size(max = 32) String relatedNcrNumber
) {
}
