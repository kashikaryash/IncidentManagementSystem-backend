package com.dto.admin;

import lombok.Data;

@Data
public class PriorityResponse {
    private Long id;
    private String name; 
    private String displayName;
    private String description;
    private Integer responseSlaMins;
    private Integer resolutionSlaMins;
    private String highlightColor;
    private boolean active;
    private boolean defaultPriority;
}