package com.dto.enduser;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndUserIncidentResponseDto {
    private Long id;
    private String shortDescription;
    private String category;
    private LocalDateTime createdAt;
    private String timeSinceCreated;
}

