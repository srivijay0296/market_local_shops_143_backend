package com.marketlocalshops.products;

import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDto(Product entity) {
        if (entity == null) {
            return null;
        }

        return ProductDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .stock(entity.getStock())
                .imageUrl(entity.getImageUrl())
                .isApproved(entity.getIsApproved())
                .viewCount(entity.getViewCount())
                .shopId(entity.getShop() != null ? entity.getShop().getId() : entity.getShopId())
                .shop(entity.getShop())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product entity = new Product();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setStock(dto.getStock());
        entity.setImageUrl(dto.getImageUrl());
        entity.setIsApproved(dto.getIsApproved());
        entity.setViewCount(dto.getViewCount());
        entity.setShopId(dto.getShopId());
        entity.setShop(dto.getShop());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }
}
