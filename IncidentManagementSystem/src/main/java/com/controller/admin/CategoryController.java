package com.controller.admin;

import com.dto.admin.CategoryDTO;
import com.dto.admin.CategoryMapper;
import com.entity.Category;
import com.service.admin.CategoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
        Category parent = (dto.getParentId() != null)
                ? categoryService.getCategoryById(dto.getParentId())
                : null;

        Category newCategory = CategoryMapper.fromDTO(dto, parent);
        Category saved = categoryService.createCategory(newCategory);

        return ResponseEntity.ok(CategoryMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        Category parent = (dto.getParentId() != null)
                ? categoryService.getCategoryById(dto.getParentId())
                : null;

        Category temp = new Category();
        temp.setName(dto.getName());
        temp.setParent(parent);
        temp.setActive(dto.isActive());
        temp.setVisibleToEndUser(dto.isVisibleToEndUser());
        temp.setDefault(dto.isDefaultCategory());

        Category updated = categoryService.updateCategory(id, temp);
        return ResponseEntity.ok(CategoryMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> dtos = categoryService.getAllCategories()
                .stream()
                .map(CategoryMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/root")
    public ResponseEntity<List<CategoryDTO>> getRootCategories() {
        List<CategoryDTO> dtos = categoryService.getRootCategories()
                .stream()
                .map(CategoryMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CategoryDTO>> getSubcategories(@PathVariable Long parentId) {
        List<CategoryDTO> dtos = categoryService.getSubcategories(parentId)
                .stream()
                .map(CategoryMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<CategoryDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(CategoryMapper.toDTO(categoryService.toggleActive(id)));
    }

    @PutMapping("/{id}/toggle-visible")
    public ResponseEntity<CategoryDTO> toggleVisible(@PathVariable Long id) {
        return ResponseEntity.ok(CategoryMapper.toDTO(categoryService.toggleVisible(id)));
    }

    @PutMapping("/{id}/set-default")
    public ResponseEntity<CategoryDTO> setDefault(@PathVariable Long id) {
        return ResponseEntity.ok(CategoryMapper.toDTO(categoryService.setAsDefault(id)));
    }

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryDTO>> getTree() {
        return ResponseEntity.ok(categoryService.getCategoryTreeDTO());
    }
}
