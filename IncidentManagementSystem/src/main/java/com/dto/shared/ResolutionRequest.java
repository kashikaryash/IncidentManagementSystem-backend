package com.dto.shared;

import lombok.Data;

@Data
public class ResolutionRequest {
    private Long resolutionCodeId;
    private Long closureCodeId;
    private String resolutionNotes;
}