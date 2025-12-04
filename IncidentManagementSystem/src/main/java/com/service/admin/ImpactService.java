// src/main/java/com/service/ImpactService.java
package com.service.admin;

import com.entity.Impact;
import com.dto.admin.ImpactDTO;
import com.repository.ImpactRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImpactService {

    private final ImpactRepository impactRepository;

    /**
     * Finds all Impact entities, converts them to DTOs, and sorts by ID.
     */
    public List<ImpactDTO> findAllImpacts() {
        return impactRepository.findAll().stream()
                .map(this::convertToDTO)
                // Sorting by Impact ID (id) to mimic the table order
                .sorted((d1, d2) -> d1.getId().compareTo(d2.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds a single Impact by ID.
     */
    public Optional<ImpactDTO> findImpactById(Long id) {
        return impactRepository.findById(id).map(this::convertToDTO);
    }
    
    /**
     * Saves a new Impact or updates an existing one.
     */
    public ImpactDTO saveImpact(ImpactDTO impactDTO) {
        Impact impact = convertToEntity(impactDTO);
        Impact savedImpact = impactRepository.save(impact);
        return convertToDTO(savedImpact);
    }
    
    /**
     * Deletes an Impact by ID.
     */
    public void deleteImpact(Long id) {
        impactRepository.deleteById(id);
    }

    /**
     * Converts Impact entity to ImpactDTO.
     */
    private ImpactDTO convertToDTO(Impact impact) {
        return ImpactDTO.builder()
                .id(impact.getId())
                .level(impact.getLevel())
                .active(impact.isActive())
                .isDefault(impact.isDefault())
                .sortOrder(impact.getSortOrder())
                .build();
    }

    /**
     * Converts ImpactDTO to Impact entity.
     */
    private Impact convertToEntity(ImpactDTO dto) {
        // If updating, fetch existing entity to avoid overwriting unmapped fields
        Impact impact = dto.getId() != null ? 
                        impactRepository.findById(dto.getId()).orElse(new Impact()) : 
                        new Impact();
                        
        // Set fields from DTO
        impact.setLevel(dto.getLevel());
        impact.setActive(dto.isActive());
        impact.setDefault(dto.isDefault());
        impact.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        
        return impact;
    }
}