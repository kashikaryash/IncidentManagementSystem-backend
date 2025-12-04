package com.dto.shared;

import lombok.Data;

@Data
public class IncidentUpdateDto {
    private String status;
    private String shortDescription;
    private String detailedDescription;
    private String location;
    private String contactType;
    private Long categoryId;
    private Long classificationId;
    private Long priorityId;
    private Long impactId;
    private Long urgencyId;
    private Long assignmentGroupId;
    private Long assignedToUserId;
    private Long pendingReasonId;
    private String workNotes;
    private String customerComments;
    private Long resolutionCodeId;
    private Long closureCodeId;
    private String resolutionNotes;
}
