package com.controller.user;

import com.dto.enduser.EndUserIncidentRequest;
import com.dto.enduser.EndUserIncidentServiceInterface;
import com.entity.Incident;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/incidents/enduser")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class EndUserIncidentController {

    private final EndUserIncidentServiceInterface incidentService;

    @PostMapping("/create")
    public ResponseEntity<Incident> createIncident(@ModelAttribute EndUserIncidentRequest request) {
        Incident created = incidentService.createEndUserIncident(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        Incident incident = incidentService.getById(id);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(incident);
    }

    @GetMapping
    public ResponseEntity<List<Incident>> getAllIncidents() {
        List<Incident> incidents = incidentService.getAll();
        return ResponseEntity.ok(incidents);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIncident(@PathVariable Long id) {
        incidentService.delete(id);
        return ResponseEntity.ok("Incident soft-deleted");
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Incident>> getIncidentsByUsername(@PathVariable String username) {
        List<Incident> incidents = incidentService.findByCreatedBy(username);
        return ResponseEntity.ok(incidents);
    }
}
