package com.dto.enduser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndUserIncidentDetailDto {
    private Long id;
    private String shortDescription;
    private String category;
    private String callerName;
    private String callerEmail;
    private String location;
    private String detailedDescription;
    private String status;
    private String resolutionNotes; 
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private List<String> attachments;
}