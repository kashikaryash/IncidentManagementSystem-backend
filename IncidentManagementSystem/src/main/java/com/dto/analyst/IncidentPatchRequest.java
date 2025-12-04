package com.dto.analyst;

import com.entity.*;
import lombok.Data;

@Data
public class IncidentPatchRequest {

    private Status status;
    private String shortDescription;
    private String detailedDescription;

    private Long priorityId;
    private Long impactId;
    private Long urgencyId;
    private Long classificationId;
    private Long categoryId;
    private Long pendingReasonId;
    private Long resolutionCodeId;
    private Long closureCodeId;
    private Long assignmentGroupId;
    private Long assignedToUserId;

    private Long callerId;
    private String callerName;
    private String callerEmail;
    private String contactNumber;
    private String contactType;
    private String location;
    private String createdBy;

    private String workNotes;
    private String customerComments;
    private String resolutionNotes;
}
