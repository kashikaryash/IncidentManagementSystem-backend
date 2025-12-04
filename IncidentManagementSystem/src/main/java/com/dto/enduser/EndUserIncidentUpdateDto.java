package com.dto.enduser;

import lombok.Data;
@Data
public class EndUserIncidentUpdateDto {
    private String status;           
    private String shortDescription;
    private String detailedDescription;
    private String resolutionNotes;
    private String location;
    private Long categoryId;
    private Long priorityId;
    private Long assignedToUserId;        
}
