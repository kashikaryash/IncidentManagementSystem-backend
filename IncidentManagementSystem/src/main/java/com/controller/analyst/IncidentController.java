package com.controller.analyst;

import com.dto.analyst.AnalystIncidentResponseDto;
import com.dto.shared.IncidentRequest;
import com.entity.Attachment;
import com.entity.Incident;
import com.entity.Status;
import com.service.analyst.IncidentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000"}, allowCredentials = "true")
public class IncidentController {

    private static final Logger log = LoggerFactory.getLogger(IncidentController.class);

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping(value = "/create-with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalystIncidentResponseDto> createIncidentWithFiles(
            @RequestPart("incident") IncidentRequest incidentRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Incident saved = incidentService.createIncidentFromRequest(incidentRequest, files);
        log.info("✔ Incident created: INC{}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incidentService.mapToAnalystDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalystIncidentResponseDto> getIncidentById(@PathVariable Long id) {
        Incident incident = incidentService.getIncidentById(id);
        return ResponseEntity.ok(incidentService.mapToAnalystDto(incident));
    }

    @GetMapping
    public ResponseEntity<List<AnalystIncidentResponseDto>> getAllIncidents() {
        List<AnalystIncidentResponseDto> dtos = incidentService.getAllIncidentDtos();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/incidents-i-raised")
    public ResponseEntity<List<AnalystIncidentResponseDto>> getIncidentsIRaised() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : null;
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        List<AnalystIncidentResponseDto> raised = incidentService.getIncidentsByCaller(email);
        return ResponseEntity.ok(raised);
    }

    @GetMapping("/my-assigned-incidents")
    public ResponseEntity<List<AnalystIncidentResponseDto>> getMyAssignedIncidents(
            @RequestHeader("X-User-Email") String email) {
        List<AnalystIncidentResponseDto> list = incidentService.getIncidentsByAssignee(email);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AnalystIncidentResponseDto> updateIncidentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Long assignedToUserId,
            @RequestParam(required = false) Long assignmentGroupId) {

        Status s;
        try {
            s = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }

        Incident updated = incidentService.updateIncidentStatus(id, s, assignedToUserId, assignmentGroupId);
        log.info("✔ Incident INC{} updated: {}", id, s);
        return ResponseEntity.ok(incidentService.mapToAnalystDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        log.warn("⚠ Incident INC{} deleted.", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/attachment/{id}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Attachment attachment = incidentService.getAttachmentById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Attachment not found with ID: " + id));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(attachment.getFileData());
    }

    @DeleteMapping("/attachment/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        incidentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/approaching-sla")
    public ResponseEntity<Map<String, Object>> getApproachingSla() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null) ? auth.getName() : "anonymous";
        return ResponseEntity.ok(Map.of(
                "message", "SLA Monitoring endpoint placeholder.",
                "user", user,
                "timestamp", System.currentTimeMillis()
        ));
    }
    
    @PostMapping("/{id}/resolve")
    public ResponseEntity<AnalystIncidentResponseDto> resolveIncident(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String notes = payload.get("notes");
        Incident updated = incidentService.resolveIncident(id, notes);
        return ResponseEntity.ok(incidentService.mapToAnalystDto(updated));
    }
}
