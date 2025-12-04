package com.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncidentUpdateAdminDTO {

    @NotNull(message = "Incident ID must be provided for update.")
    private Long id;

    private String shortDescription;
    private String detailedDescription;

    private String status;
    private Long pendingReasonId;
    private Long resolutionCodeId;
    private Long closureCodeId;

    private Long priorityId;
    private Long impactId;
    private Long urgencyId;
    private Long classificationId;
    private Long categoryId;

    private Long assignmentGroupId;
    private Long assignedToUserId;

    private String workNotes;
    private String customerComments;
    private String resolutionNotes;
}
