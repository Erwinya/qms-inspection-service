package com.halukkilincer.inspection.service;

import com.halukkilincer.inspection.domain.Inspection;
import com.halukkilincer.inspection.domain.InspectionResult;
import com.halukkilincer.inspection.domain.InspectionStatus;
import com.halukkilincer.inspection.dto.CreateInspectionRequest;
import com.halukkilincer.inspection.dto.InspectionResponse;
import com.halukkilincer.inspection.dto.UpdateInspectionRequest;
import com.halukkilincer.inspection.dto.UpdateInspectionStatusRequest;
import com.halukkilincer.inspection.exception.BadRequestException;
import com.halukkilincer.inspection.exception.NotFoundException;
import com.halukkilincer.inspection.repository.InspectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
public class InspectionService {

    private final InspectionRepository repository;

    public InspectionService(InspectionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public InspectionResponse create(CreateInspectionRequest request) {
        Inspection inspection = new Inspection();
        inspection.setInspectionNumber(nextInspectionNumber());
        inspection.setTitle(request.title().trim());
        inspection.setChecklistSummary(request.checklistSummary().trim());
        inspection.setStatus(InspectionStatus.PLANNED);
        inspection.setResult(InspectionResult.PENDING);
        inspection.setLotNumber(trimToNull(request.lotNumber()));
        inspection.setPartNumber(trimToNull(request.partNumber()));
        inspection.setInspector(request.inspector().trim());
        return InspectionResponse.from(repository.save(inspection));
    }

    @Transactional(readOnly = true)
    public InspectionResponse getById(Long id) {
        return InspectionResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<InspectionResponse> list(InspectionStatus status, InspectionResult result) {
        List<Inspection> items;
        if (status != null) {
            items = repository.findByStatusOrderByCreatedAtDesc(status);
        } else if (result != null) {
            items = repository.findByResultOrderByCreatedAtDesc(result);
        } else {
            items = repository.findAllByOrderByCreatedAtDesc();
        }
        return items.stream().map(InspectionResponse::from).toList();
    }

    @Transactional
    public InspectionResponse update(Long id, UpdateInspectionRequest request) {
        Inspection inspection = find(id);
        if (inspection.getStatus() == InspectionStatus.COMPLETED
                || inspection.getStatus() == InspectionStatus.CANCELLED) {
            throw new BadRequestException("Completed or cancelled inspections cannot be edited");
        }
        if (request.title() != null && !request.title().isBlank()) {
            inspection.setTitle(request.title().trim());
        }
        if (request.checklistSummary() != null && !request.checklistSummary().isBlank()) {
            inspection.setChecklistSummary(request.checklistSummary().trim());
        }
        if (request.lotNumber() != null) {
            inspection.setLotNumber(trimToNull(request.lotNumber()));
        }
        if (request.partNumber() != null) {
            inspection.setPartNumber(trimToNull(request.partNumber()));
        }
        if (request.notes() != null) {
            inspection.setNotes(trimToNull(request.notes()));
        }
        if (request.result() != null) {
            inspection.setResult(request.result());
        }
        if (request.relatedNcrNumber() != null) {
            inspection.setRelatedNcrNumber(trimToNull(request.relatedNcrNumber()));
        }
        return InspectionResponse.from(repository.save(inspection));
    }

    @Transactional
    public InspectionResponse updateStatus(Long id, UpdateInspectionStatusRequest request) {
        Inspection inspection = find(id);
        InspectionStatus next = request.status();
        if (!inspection.getStatus().canTransitionTo(next)) {
            throw new BadRequestException(
                    "Invalid status transition: " + inspection.getStatus() + " -> " + next
            );
        }

        if (next == InspectionStatus.COMPLETED) {
            InspectionResult result = request.result();
            if (result == null || result == InspectionResult.PENDING) {
                throw new BadRequestException("Completed inspections require a final result (PASS, FAIL, or CONDITIONAL)");
            }
            inspection.setResult(result);
        }

        inspection.setStatus(next);
        if (request.relatedNcrNumber() != null) {
            inspection.setRelatedNcrNumber(trimToNull(request.relatedNcrNumber()));
        }
        if (request.note() != null && !request.note().isBlank()) {
            String existing = inspection.getNotes();
            String addition = "[" + next + "] " + request.note().trim();
            inspection.setNotes(existing == null || existing.isBlank()
                    ? addition
                    : existing + "\n" + addition);
        }
        return InspectionResponse.from(repository.save(inspection));
    }

    private Inspection find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inspection not found: " + id));
    }

    private String nextInspectionNumber() {
        String prefix = "INS-" + Year.now().getValue() + "-";
        long count = repository.countByInspectionNumberStartingWith(prefix);
        return prefix + String.format("%04d", count + 1);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
