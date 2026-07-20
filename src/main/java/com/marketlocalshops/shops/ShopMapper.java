package com.marketlocalshops.shops;

import org.springframework.stereotype.Component;

@Component
public class ShopMapper {

    public ShopDTO toDto(Shop entity) {
        if (entity == null) {
            return null;
        }

        return ShopDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .owner(entity.getOwner())
                .market(entity.getMarket())
                .category(entity.getCategory())
                .status(entity.getStatus())
                .imageUrl(entity.getImageUrl())
                .vendorName(entity.getVendorName())
                .location(entity.getLocation())
                .phone(entity.getPhone())
                .ownerId(entity.getOwner() != null ? entity.getOwner().getId() : entity.getOwnerId())
                .marketId(entity.getMarket() != null ? entity.getMarket().getId() : entity.getMarketId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Shop toEntity(ShopDTO dto) {
        if (dto == null) {
            return null;
        }

        Shop entity = new Shop();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setOwner(dto.getOwner());
        entity.setMarket(dto.getMarket());
        entity.setCategory(dto.getCategory());
        entity.setStatus(dto.getStatus());
        entity.setImageUrl(dto.getImageUrl());
        entity.setVendorName(dto.getVendorName());
        entity.setLocation(dto.getLocation());
        entity.setPhone(dto.getPhone());
        entity.setOwnerId(dto.getOwnerId());
        entity.setMarketId(dto.getMarketId());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }
}
