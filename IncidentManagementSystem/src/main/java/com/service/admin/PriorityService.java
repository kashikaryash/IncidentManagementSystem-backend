package com.service.admin;

import com.dto.admin.PriorityRequest;
import com.dto.admin.PriorityResponse;
import com.entity.Priority;
import com.repository.PriorityRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PriorityService {

    private final PriorityRepository priorityRepository;

    public PriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    public List<Priority> getAllPriorities() {
        return priorityRepository.findAll();
    }

    public List<Priority> getActivePriorities() {
        return priorityRepository.findAllByActiveTrue();
    }

    public Priority getPriorityById(Long id) {
        return priorityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Priority not found with ID: " + id));
    }

    @Transactional
    public PriorityResponse createPriority(@Valid PriorityRequest request) {

        // ... existing name check (now handled by @NotBlank in DTO, but kept for clarity)
        // if (request.getName() == null || request.getName().isBlank()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Priority name cannot be empty.");
        // }

        if (request.isDefaultPriority()) {
            clearOtherDefaultPriorities(null);
        }

        Priority priority = Priority.builder()
                .name(request.getName())
                // START: New fields added to builder
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .responseSlaMins(request.getResponseSlaMins())
                .resolutionSlaMins(request.getResolutionSlaMins())
                .highlightColor(request.getHighlightColor())
                // END: New fields added to builder
                .active(request.isActive())
                .defaultPriority(request.isDefaultPriority())
                .build();

        Priority saved = priorityRepository.save(priority);

        return toResponse(saved);
    }

 // ... inside PriorityService class

    @Transactional
    public PriorityResponse updatePriority(Long id, @Valid PriorityRequest request) {

        Priority existing = getPriorityById(id);

        if (request.getName() != null) {
            // if (request.getName().isBlank()) { ... check handled by @NotBlank }
            existing.setName(request.getName());
        }

        // START: Update new fields
        existing.setDisplayName(request.getDisplayName());
        existing.setDescription(request.getDescription());
        existing.setResponseSlaMins(request.getResponseSlaMins());
        existing.setResolutionSlaMins(request.getResolutionSlaMins());
        existing.setHighlightColor(request.getHighlightColor());
        // END: Update new fields

        existing.setActive(request.isActive());

        boolean isChangingToDefault =
                !existing.isDefaultPriority() && request.isDefaultPriority();

        existing.setDefaultPriority(request.isDefaultPriority());

        if (isChangingToDefault) {
            clearOtherDefaultPriorities(id);
        }

        Priority saved = priorityRepository.save(existing);

        return toResponse(saved);
    }    

    @Transactional
    private void clearOtherDefaultPriorities(Long currentId) {

        List<Priority> all = priorityRepository.findAll();

        for (Priority p : all) {
            if (p.isDefaultPriority()
                    && (currentId == null || !p.getId().equals(currentId))) {

                p.setDefaultPriority(false);
                priorityRepository.save(p);
            }
        }
    }

    @Transactional
    public void deletePriority(Long id) {
        if (!priorityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Priority not found with ID: " + id);
        }
        priorityRepository.deleteById(id);
    }

    /**
     * Convert Entity → Response DTO
     */
    private PriorityResponse toResponse(Priority priority) {
        PriorityResponse res = new PriorityResponse();
        res.setId(priority.getId());
        res.setName(priority.getName());
        // START: Map new fields
        res.setDisplayName(priority.getDisplayName());
        res.setDescription(priority.getDescription());
        res.setResponseSlaMins(priority.getResponseSlaMins());
        res.setResolutionSlaMins(priority.getResolutionSlaMins());
        res.setHighlightColor(priority.getHighlightColor());
        // END: Map new fields
        res.setActive(priority.isActive());
        res.setDefaultPriority(priority.isDefaultPriority());
        return res;
    }
}
