package com.marketlocalshops.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO updateCategory(Long id, CategoryDTO updates) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("Category not found with id: " + id));
        if (updates.getName() != null) category.setName(updates.getName());
        if (updates.getSlug() != null) category.setSlug(updates.getSlug());
        if (updates.getImageUrl() != null) category.setImageUrl(updates.getImageUrl());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }
}
