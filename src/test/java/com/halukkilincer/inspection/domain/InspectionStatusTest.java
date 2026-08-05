package com.halukkilincer.inspection.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InspectionStatusTest {

    @Test
    void allowsExpectedTransitions() {
        assertThat(InspectionStatus.PLANNED.canTransitionTo(InspectionStatus.IN_PROGRESS)).isTrue();
        assertThat(InspectionStatus.PLANNED.canTransitionTo(InspectionStatus.COMPLETED)).isFalse();
        assertThat(InspectionStatus.COMPLETED.canTransitionTo(InspectionStatus.IN_PROGRESS)).isFalse();
    }
}
