package com.dto.analyst;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalystIncidentResponseDto {

    private Long id;
    private String shortDescription;
    private String detailedDescription;
    private String priorityName;
    private String priorityLevel;
    private String status;
    private String caller;
    private String callerEmail;
    private String contactNumber;
    private String location;
    private String category;
    private Long assignedToUserId;
    private String assignmentGroup;
    private String responseTimeRemaining;
    private String resolutionTimeRemaining;
    private String createdAt;
}
