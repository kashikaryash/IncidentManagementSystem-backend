package com.repository;

import com.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentIsNull();

    List<Category> findByParent_Id(Long parentId);

    Optional<Category> findByNameAndParentIsNull(String name);

    Optional<Category> findByNameAndParent_Id(String name, Long parentId);

    Optional<Category> findByName(String name);
}
