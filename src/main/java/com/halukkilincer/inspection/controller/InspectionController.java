package com.halukkilincer.inspection.controller;

import com.halukkilincer.inspection.domain.InspectionResult;
import com.halukkilincer.inspection.domain.InspectionStatus;
import com.halukkilincer.inspection.dto.CreateInspectionRequest;
import com.halukkilincer.inspection.dto.InspectionResponse;
import com.halukkilincer.inspection.dto.UpdateInspectionRequest;
import com.halukkilincer.inspection.dto.UpdateInspectionStatusRequest;
import com.halukkilincer.inspection.response.ApiResponse;
import com.halukkilincer.inspection.service.InspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inspections")
@Tag(name = "Inspections", description = "Create and manage quality inspections")
public class InspectionController {

    private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @PostMapping
    @Operation(summary = "Create a planned inspection")
    public ResponseEntity<ApiResponse<InspectionResponse>> create(@Valid @RequestBody CreateInspectionRequest request) {
        InspectionResponse created = inspectionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, 201));
    }

    @GetMapping
    @Operation(summary = "List inspections, optionally filtered by status or result")
    public ResponseEntity<ApiResponse<List<InspectionResponse>>> list(
            @RequestParam(required = false) InspectionStatus status,
            @RequestParam(required = false) InspectionResult result
    ) {
        return ResponseEntity.ok(ApiResponse.success(inspectionService.list(status, result), 200));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inspection by id")
    public ResponseEntity<ApiResponse<InspectionResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(inspectionService.getById(id), 200));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update inspection details (not allowed when completed/cancelled)")
    public ResponseEntity<ApiResponse<InspectionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInspectionRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(inspectionService.update(id, request), 200));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Transition inspection status along the allowed workflow")
    public ResponseEntity<ApiResponse<InspectionResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInspectionStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(inspectionService.updateStatus(id, request), 200));
    }
}
