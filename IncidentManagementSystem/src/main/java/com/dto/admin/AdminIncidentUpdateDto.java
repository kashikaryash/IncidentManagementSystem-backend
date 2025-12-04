package com.dto.admin;

import lombok.Data;

@Data
public class AdminIncidentUpdateDto {

    private Long priorityId;
    private Long impactId;
    private Long urgencyId;
    private Long categoryId;
    private Long classificationId;

    private Long assignmentGroupId;
    private Long assignedToUserId;

    private String status;
    private String contactType;
    private String location;

    private String shortDescription;
    private String detailedDescription;
    private String workNotes;
    private String customerComments;

    private Long callerId;
}
