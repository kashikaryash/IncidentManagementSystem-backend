package com.controller.admin;

import com.entity.AssignmentGroup;
import com.repository.AssignmentGroupRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignment-groups")
public class AssignmentGroupController {

    private final AssignmentGroupRepository repository;

    public AssignmentGroupController(AssignmentGroupRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<AssignmentGroup>> getAllGroups() {
        List<AssignmentGroup> groups = repository.findAll();
        return ResponseEntity.ok(groups);
    }
}
