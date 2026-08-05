package com.halukkilincer.inspection.dto;

import com.halukkilincer.inspection.domain.Inspection;
import com.halukkilincer.inspection.domain.InspectionResult;
import com.halukkilincer.inspection.domain.InspectionStatus;

import java.time.Instant;

public record InspectionResponse(
        Long id,
        String inspectionNumber,
        String title,
        String checklistSummary,
        InspectionStatus status,
        InspectionResult result,
        String lotNumber,
        String partNumber,
        String inspector,
        String notes,
        String relatedNcrNumber,
        Instant createdAt,
        Instant updatedAt
) {
    public static InspectionResponse from(Inspection entity) {
        return new InspectionResponse(
                entity.getId(),
                entity.getInspectionNumber(),
                entity.getTitle(),
                entity.getChecklistSummary(),
                entity.getStatus(),
                entity.getResult(),
                entity.getLotNumber(),
                entity.getPartNumber(),
                entity.getInspector(),
                entity.getNotes(),
                entity.getRelatedNcrNumber(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
