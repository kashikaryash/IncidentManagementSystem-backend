package com.dto;

import com.entity.Status;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class IncidentRespons {
    private Long id;
    private String shortDescription;
    private String category;
    private String callerName;
    private String callerEmail;
    private String contactNumber;
    private String location;
    private String detailedDescription;
    private Status status;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private List<String> attachments; // file names only
}
