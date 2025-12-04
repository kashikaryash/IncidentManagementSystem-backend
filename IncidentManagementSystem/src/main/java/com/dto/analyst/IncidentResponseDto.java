package com.dto.analyst;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentResponseDto {

    private Long id;
    private Long assignedToUserId;
    private String shortDescription;
    private String priorityName;
    private String status;
    private String createdByName;
    private String responseTimeRemaining;
    private String resolutionTimeRemaining;
}
