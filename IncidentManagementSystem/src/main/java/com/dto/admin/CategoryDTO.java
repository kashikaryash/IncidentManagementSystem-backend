package com.dto.admin;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private boolean active;
    private boolean visibleToEndUser;
    private boolean defaultCategory;

    private List<CategoryDTO> children = new ArrayList<>();
}
