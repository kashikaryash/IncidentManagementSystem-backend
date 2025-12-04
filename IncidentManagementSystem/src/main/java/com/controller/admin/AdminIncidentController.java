package com.controller.admin;

import com.dto.admin.AdminIncidentDto;
import com.dto.admin.AdminIncidentUpdateDto;
import com.service.admin.AdminIncidentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin/incidents")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminIncidentController {

    private final AdminIncidentService adminIncidentService;

    public AdminIncidentController(AdminIncidentService adminIncidentService) {
        this.adminIncidentService = adminIncidentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdminIncidentDto>> getAllIncidents() {
        List<AdminIncidentDto> incidents = adminIncidentService.getAllIncidents();
        return ResponseEntity.ok(incidents);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateIncident(
            @PathVariable Long id, 
            @Valid @RequestBody AdminIncidentUpdateDto dto) {

        adminIncidentService.updateIncident(id, dto);
        
        return ResponseEntity.noContent().build(); 
    }
}
