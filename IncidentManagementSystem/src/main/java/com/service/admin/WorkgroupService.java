package com.service.admin;

import com.dto.admin.WorkgroupRequest; // <-- NEW IMPORT
import com.entity.User; // <-- NEW IMPORT
import com.entity.Workgroup;
import com.repository.WorkgroupRepository;
import com.repository.UserRepository; // <-- NEW REPOSITORY IMPORT
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class WorkgroupService {

    private final WorkgroupRepository workgroupRepository;
    private final UserRepository userRepository; // <-- NEW DEPENDENCY

    // Constructor Injection
    public WorkgroupService(WorkgroupRepository workgroupRepository, UserRepository userRepository) {
        this.workgroupRepository = workgroupRepository;
        this.userRepository = userRepository;
    }

    public List<Workgroup> getAllWorkgroups() {
        return workgroupRepository.findAll();
    }

    public Workgroup getWorkgroupById(Long id) {
        return workgroupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workgroup not found"));
    }

    /**
     * Create a new workgroup from the DTO.
     */
    public Workgroup createWorkgroup(WorkgroupRequest request) { // <-- CHANGED PARAMETER
        
        // 1. Fetch Owner (User) entity using ownerId from the DTO
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner user not found with ID: " + request.getOwnerId()));
        
        // 2. Map DTO fields to the new Workgroup entity
        Workgroup newWorkgroup = new Workgroup();
        newWorkgroup.setName(request.getName());
        newWorkgroup.setDisplayName(request.getDisplayName());
        newWorkgroup.setOwner(owner); // <-- SETS THE FETCHED USER ENTITY
        newWorkgroup.setDescription(request.getDescription());
        newWorkgroup.setMaster(request.getMaster());
        newWorkgroup.setDefaultWorkgroup(request.getDefaultWorkgroup());
        newWorkgroup.setActive(request.getActive());
        
        return workgroupRepository.save(newWorkgroup);
    }

    /**
     * Update an existing workgroup from the DTO.
     */
    public Workgroup updateWorkgroup(Long id, WorkgroupRequest request) { // <-- CHANGED PARAMETER
        Workgroup existing = workgroupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workgroup not found"));

        // 1. Fetch Owner (User) entity using ownerId from the DTO
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner user not found with ID: " + request.getOwnerId()));
        
        // 2. Update existing fields using the DTO
        existing.setName(request.getName());
        existing.setDisplayName(request.getDisplayName());
        existing.setOwner(owner);             // <-- SETS THE FETCHED USER ENTITY
        existing.setDescription(request.getDescription());
        existing.setMaster(request.getMaster());
        existing.setDefaultWorkgroup(request.getDefaultWorkgroup());
        existing.setActive(request.getActive());

        return workgroupRepository.save(existing);
    }

    public void deleteWorkgroup(Long id) {
        if (!workgroupRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workgroup not found");
        }
        workgroupRepository.deleteById(id);
    }
}