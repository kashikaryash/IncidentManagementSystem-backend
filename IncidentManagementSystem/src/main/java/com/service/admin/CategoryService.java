package com.service.admin;

import com.dto.admin.CategoryDTO;
import com.dto.admin.CategoryMapper;
import com.entity.Category;
import com.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	// -------------------------------------------------------------------------
	// BASIC CRUD
	// -------------------------------------------------------------------------
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	public Category createCategory(Category category) {
		return categoryRepository.save(category);
	}

	public Category getCategoryById(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
	}

	public Category updateCategory(Long id, Category updatedCategory) {
		Category existing = getCategoryById(id);

		existing.setName(updatedCategory.getName());
		existing.setActive(updatedCategory.isActive());
		existing.setVisibleToEndUser(updatedCategory.isVisibleToEndUser());
		existing.setDefault(updatedCategory.isDefault());

		if (updatedCategory.getParent() != null) {
			existing.setParent(updatedCategory.getParent());
		} else {
			existing.setParent(null);
		}

		return categoryRepository.save(existing);
	}

	public void deleteCategory(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
		}
		categoryRepository.deleteById(id);
	}

	// -------------------------------------------------------------------------
	// TOGGLES
	// -------------------------------------------------------------------------
	@Transactional
	public Category toggleActive(Long id) {
		Category c = getCategoryById(id);
		c.setActive(!c.isActive());
		return categoryRepository.save(c);
	}

	@Transactional
	public Category toggleVisible(Long id) {
		Category c = getCategoryById(id);
		c.setVisibleToEndUser(!c.isVisibleToEndUser());
		return categoryRepository.save(c);
	}

	// -------------------------------------------------------------------------
	// DEFAULT CATEGORY
	// -------------------------------------------------------------------------
	@Transactional
	public Category setAsDefault(Long id) {

		List<Category> all = categoryRepository.findAll();

		// unset all defaults
		all.forEach(c -> c.setDefault(false));

		Category cat = getCategoryById(id);
		cat.setDefault(true);

		categoryRepository.saveAll(all);

		return cat;
	}

	// -------------------------------------------------------------------------
	// CATEGORY TREE
	// -------------------------------------------------------------------------
	public List<Category> getRootCategories() {
		return categoryRepository.findByParentIsNull();
	}

	public List<Category> getSubcategories(Long parentId) {
		return categoryRepository.findByParent_Id(parentId);
	}

	private void populateChildren(Category category) {
		List<Category> children = categoryRepository.findByParent_Id(category.getId());
		category.setSubcategories(children);
		children.forEach(this::populateChildren);
	}

	public List<Category> getCategoryTree() {
		List<Category> roots = categoryRepository.findByParentIsNull();
		roots.forEach(this::populateChildren);
		return roots;
	}

	public List<CategoryDTO> getCategoryTreeDTO() {
		List<Category> roots = categoryRepository.findByParentIsNull();
		return roots.stream().map(this::mapToTreeDTO).toList();
	}

	private CategoryDTO mapToTreeDTO(Category category) {
		CategoryDTO dto = CategoryMapper.toDTO(category);

		List<Category> children = categoryRepository.findByParent_Id(category.getId());

		dto.setChildren(children.stream().map(this::mapToTreeDTO).toList());

		return dto;
	}

	// -------------------------------------------------------------------------
	// CATEGORY PATH RESOLVER
	// "Hardware > Laptop > Battery"
	// -------------------------------------------------------------------------
	public Category findCategoryFromPath(String path) {

	    if (path == null || path.trim().isEmpty()) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category path cannot be empty");
	    }

	    String[] parts = path.split(">");

	    for (int i = 0; i < parts.length; i++) {
	        parts[i] = parts[i].trim();
	    }

	    final Category[] current = {null};

	    for (String part : parts) {
	        if (current[0] == null) {
	            current[0] = categoryRepository.findByNameAndParentIsNull(part)
	                    .orElseThrow(() -> new ResponseStatusException(
	                            HttpStatus.BAD_REQUEST, "Invalid root category: " + part));
	        } else {
	            current[0] = categoryRepository.findByNameAndParent_Id(part, current[0].getId())
	                    .orElseThrow(() -> new ResponseStatusException(
	                            HttpStatus.BAD_REQUEST,
	                            "Invalid subcategory `" + part + "` under `" + current[0].getName() + "`"));
	        }
	    }

	    return current[0];
	}

}