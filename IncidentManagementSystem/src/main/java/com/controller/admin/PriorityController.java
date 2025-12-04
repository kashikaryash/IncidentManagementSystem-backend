package com.controller.admin;

import com.dto.admin.PriorityRequest;
import com.dto.admin.PriorityResponse;
import com.entity.Priority;
import com.service.admin.PriorityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/priorities")
public class PriorityController {

    private final PriorityService priorityService;

    public PriorityController(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @GetMapping
    public ResponseEntity<List<Priority>> getAllPriorities() {
        return ResponseEntity.ok(priorityService.getAllPriorities());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Priority>> getActivePriorities() {
        return ResponseEntity.ok(priorityService.getActivePriorities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Priority> getPriorityById(@PathVariable Long id) {
        return ResponseEntity.ok(priorityService.getPriorityById(id));
    }

    @PostMapping
    public ResponseEntity<PriorityResponse> createPriority(
            @Valid @RequestBody PriorityRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(priorityService.createPriority(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriorityResponse> updatePriority(
            @PathVariable Long id,
            @Valid @RequestBody PriorityRequest request) {

        return ResponseEntity.ok(priorityService.updatePriority(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePriority(@PathVariable Long id) {
        priorityService.deletePriority(id);
        return ResponseEntity.noContent().build();
    }
}
