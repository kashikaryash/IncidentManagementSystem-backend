package com.dto.admin;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminIncidentDto {
    private Long id;
    private String status;
    private String priority;
    private String impact;
    private String urgency;
    private String classification;
    private String category;
    private String pendingReason;
    private String resolutionCode;
    private String closureCode;

    private String caller;
    private String contactType;
    private String location;

    private String assignmentGroup;
    private String assignedTo;
    private String shortDescription;
    private String detailedDescription;
    private String workNotes;
    private String customerComments;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime responseDueBy;
    private LocalDateTime resolutionDueBy;
}