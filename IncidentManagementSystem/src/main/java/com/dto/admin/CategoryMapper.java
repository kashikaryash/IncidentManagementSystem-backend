package com.dto.admin;

import com.entity.Category;

public class CategoryMapper {

    public static CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setActive(category.isActive());
        dto.setVisibleToEndUser(category.isVisibleToEndUser());
        dto.setDefaultCategory(category.isDefault());
        return dto;
    }

    public static Category fromDTO(CategoryDTO dto, Category parent) {
        Category cat = new Category();

        cat.setName(dto.getName());
        cat.setParent(parent);
        cat.setActive(dto.isActive());
        cat.setVisibleToEndUser(dto.isVisibleToEndUser());
        cat.setDefault(dto.isDefaultCategory());

        return cat;
    }
}
