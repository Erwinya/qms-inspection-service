package com.halukkilincer.inspection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateInspectionRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 4000) String checklistSummary,
        @Size(max = 100) String lotNumber,
        @Size(max = 100) String partNumber,
        @NotBlank @Size(max = 120) String inspector
) {
}
