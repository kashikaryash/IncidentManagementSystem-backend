// src/main/java/com/dto/ImpactDTO.java
package com.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactDTO {
    private Long id;
    private String level;
    private boolean active;
    private boolean isDefault;
    private Integer sortOrder;
}