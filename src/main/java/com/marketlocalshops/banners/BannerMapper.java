package com.marketlocalshops.banners;

import org.springframework.stereotype.Component;

@Component
public class BannerMapper {
    public BannerDTO toDto(Banner entity) {
        if (entity == null) return null;
        return BannerDTO.builder()
                .id(entity.getId())
                .imageUrl(entity.getImageUrl())
                .active(entity.getActive())
                .sortOrder(entity.getSortOrder())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Banner toEntity(BannerDTO dto) {
        if (dto == null) return null;
        Banner entity = new Banner();
        entity.setId(dto.getId());
        entity.setImageUrl(dto.getImageUrl());
        entity.setActive(dto.getActive());
        entity.setSortOrder(dto.getSortOrder());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
