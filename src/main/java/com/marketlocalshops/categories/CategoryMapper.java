package com.marketlocalshops.categories;

import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryDTO toDto(Category entity) {
        if (entity == null) return null;
        return CategoryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public Category toEntity(CategoryDTO dto) {
        if (dto == null) return null;
        Category entity = new Category();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
}
