package com.controller.admin;

import com.entity.Classification;
import com.service.admin.ClassificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/classifications")
@CrossOrigin(origins = "https://incident-management-frontend.vercel.app", allowCredentials = "true")
public class ClassificationController {

    private final ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @PostMapping
    public ResponseEntity<Classification> createClassification(@RequestBody Classification classification) {
        Classification created = classificationService.createClassification(classification);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Classification>> getAllClassifications() {
        return ResponseEntity.ok(classificationService.getAllClassifications());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classification> updateClassification(@PathVariable Long id, @RequestBody Classification classification) {
        Classification updated = classificationService.updateClassification(id, classification);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClassification(@PathVariable Long id) {
        classificationService.deleteClassification(id);
        return ResponseEntity.ok("Classification deleted successfully");
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<Classification> toggleActive(@PathVariable Long id) {
        Classification updated = classificationService.toggleActive(id);
        return ResponseEntity.ok(updated);
    }
}
