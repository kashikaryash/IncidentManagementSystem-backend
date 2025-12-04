package com.controller.admin;

import com.dto.admin.WorkgroupRequest;
import com.entity.Workgroup;
import com.service.admin.WorkgroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workgroups")
@CrossOrigin(origins = "*")
public class WorkgroupController {

    @Autowired
    private WorkgroupService workgroupService;

    @GetMapping
    public ResponseEntity<List<Workgroup>> getAll() {
        return ResponseEntity.ok(workgroupService.getAllWorkgroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workgroup> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workgroupService.getWorkgroupById(id));
    }

    @PostMapping
    public ResponseEntity<Workgroup> create(@RequestBody WorkgroupRequest request) { 
        Workgroup createdWorkgroup = workgroupService.createWorkgroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWorkgroup);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workgroup> update(@PathVariable Long id,
                                            @RequestBody WorkgroupRequest request) {
        return ResponseEntity.ok(workgroupService.updateWorkgroup(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workgroupService.deleteWorkgroup(id);
        return ResponseEntity.noContent().build();
    }
}
