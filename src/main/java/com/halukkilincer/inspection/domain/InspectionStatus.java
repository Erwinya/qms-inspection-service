package com.halukkilincer.inspection.domain;

import java.util.EnumSet;
import java.util.Set;

public enum InspectionStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(InspectionStatus next) {
        return allowedTransitions().contains(next);
    }

    private Set<InspectionStatus> allowedTransitions() {
        return switch (this) {
            case PLANNED -> EnumSet.of(IN_PROGRESS, CANCELLED);
            case IN_PROGRESS -> EnumSet.of(COMPLETED, CANCELLED);
            case COMPLETED, CANCELLED -> EnumSet.noneOf(InspectionStatus.class);
        };
    }
}
