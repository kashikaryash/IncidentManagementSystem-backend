package com.dto.shared;

import lombok.Data;

@Data
public class IncidentRequest {

    private String shortDescription;
    private String category;
    private String detailedDescription;
    private String callerName;
    private String callerEmail;
    private String contactNumber;
    private String location;
}
