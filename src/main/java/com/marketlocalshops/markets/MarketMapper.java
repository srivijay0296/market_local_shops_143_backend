package com.marketlocalshops.markets;

import org.springframework.stereotype.Component;

@Component
public class MarketMapper {

    public MarketDTO toDto(Market entity) {
        if (entity == null) {
            return null;
        }

        return MarketDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .location(entity.getLocation())
                .status(entity.getStatus())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Market toEntity(MarketDTO dto) {
        if (dto == null) {
            return null;
        }

        Market entity = new Market();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLocation(dto.getLocation());
        entity.setStatus(dto.getStatus());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setImageUrl(dto.getImageUrl());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }
}
