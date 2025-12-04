package com.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkgroupRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    private String displayName;

    @NotNull
    private Long ownerId;

    private String description;

    private Boolean master = false;
    private Boolean defaultWorkgroup = false;
    private Boolean active = true;
}
