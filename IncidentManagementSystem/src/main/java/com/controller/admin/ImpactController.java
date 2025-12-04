package com.controller.admin;

import com.dto.admin.ImpactDTO;
import com.service.admin.ImpactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/impacts")
@RequiredArgsConstructor
public class ImpactController {

    private final ImpactService impactService;

    @GetMapping
    public ResponseEntity<List<ImpactDTO>> getAllImpacts() {
        List<ImpactDTO> impacts = impactService.findAllImpacts();
        return ResponseEntity.ok(impacts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImpactDTO> getImpactById(@PathVariable Long id) {
        return impactService.findImpactById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ImpactDTO> createImpact(@RequestBody ImpactDTO impactDTO) {
        impactDTO.setId(null);
        ImpactDTO createdImpact = impactService.saveImpact(impactDTO);
        return new ResponseEntity<>(createdImpact, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImpactDTO> updateImpact(@PathVariable Long id, @RequestBody ImpactDTO impactDTO) {
        if (impactService.findImpactById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        impactDTO.setId(id);
        ImpactDTO updatedImpact = impactService.saveImpact(impactDTO);
        return ResponseEntity.ok(updatedImpact);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImpact(@PathVariable Long id) {
        impactService.deleteImpact(id);
        return ResponseEntity.noContent().build();
    }
}
