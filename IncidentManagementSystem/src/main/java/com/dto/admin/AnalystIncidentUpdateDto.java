package com.dto.admin;

import lombok.Data;

@Data
public class AnalystIncidentUpdateDto {
    private String status;
    private String shortDescription;
    private String detailedDescription;
    private String workNotes;
    private String customerComments;
    private String location;
    private Long categoryId;
    private Long priorityId;
    private Long pendingReasonId;
    private Long assignmentGroupId;
    private Long assignedToUserId;
}
