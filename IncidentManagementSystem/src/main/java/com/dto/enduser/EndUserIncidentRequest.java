package com.dto.enduser;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EndUserIncidentRequest {

    private String username;
    private String callerName;
    private String callerEmail;
    private String shortDescription;
    private String detailedDescription;
    private Long categoryId;

    private MultipartFile[] attachments;
}
