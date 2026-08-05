package com.halukkilincer.inspection.repository;

import com.halukkilincer.inspection.domain.Inspection;
import com.halukkilincer.inspection.domain.InspectionResult;
import com.halukkilincer.inspection.domain.InspectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    List<Inspection> findAllByOrderByCreatedAtDesc();

    List<Inspection> findByStatusOrderByCreatedAtDesc(InspectionStatus status);

    List<Inspection> findByResultOrderByCreatedAtDesc(InspectionResult result);

    long countByInspectionNumberStartingWith(String prefix);
}
