package com.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PriorityRequest {

    @NotBlank(message = "Priority name (e.g. P1, High) cannot be empty")
    private String name;

    @NotBlank(message = "Display name cannot be empty")
    private String displayName;

    private String description;

    @NotNull(message = "Response SLA in minutes is required")
    private Integer responseSlaMins;

    @NotNull(message = "Resolution SLA in minutes is required")
    private Integer resolutionSlaMins;
    
    @NotBlank(message = "Highlight color is required")
    private String highlightColor;

    private boolean active = true;

    private boolean defaultPriority = false;
}