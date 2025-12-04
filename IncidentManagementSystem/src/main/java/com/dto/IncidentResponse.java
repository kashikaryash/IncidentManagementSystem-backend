package com.dto;

import com.entity.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidentResponse {

    private Long id;
    private Status status;
    private String shortDescription;
    private String detailedDescription;

    private Priority priority;
    private Impact impact;
    private Urgency urgency;
    private Classification classification;
    private Category category;
    private PendingReason pendingReason;
    private ResolutionCode resolutionCode;
    private ClosureCode closureCode;
    private Workgroup assignmentGroup;

    private User assignedTo;
    private User createdByUser;
    private User callerUser;

    private String callerName;
    private String callerEmail;
    private String contactNumber;
    private String contactType;
    private String location;
    private String createdBy;

    private String workNotes;
    private String customerComments;
    private String resolutionNotes;

    private LocalDateTime deletedAt;
    private LocalDateTime responseDueBy;
    private LocalDateTime resolutionDueBy;
    private LocalDateTime resolvedAt;
}
