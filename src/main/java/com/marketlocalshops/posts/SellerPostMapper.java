package com.marketlocalshops.posts;

import org.springframework.stereotype.Component;

@Component
public class SellerPostMapper {

    public SellerPostDTO toDto(SellerPost entity) {
        if (entity == null) {
            return null;
        }

        return SellerPostDTO.builder()
                .id(entity.getId())
                .shop(entity.getShop())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .mediaUrl(entity.getMediaUrl())
                .mediaType(entity.getMediaType())
                .videoUrl(entity.getVideoUrl())
                .price(entity.getPrice())
                .offerTag(entity.getOfferTag())
                .location(entity.getLocation())
                .category(entity.getCategory())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .mediaUrls(entity.getMediaUrls())
                .shopId(entity.getShop() != null ? entity.getShop().getId() : entity.getShopId())
                .sellerId(entity.getSellerId())
                .build();
    }

    public SellerPost toEntity(SellerPostDTO dto) {
        if (dto == null) {
            return null;
        }

        SellerPost entity = new SellerPost();
        entity.setId(dto.getId());
        entity.setShop(dto.getShop());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setMediaUrl(dto.getMediaUrl());
        entity.setMediaType(dto.getMediaType());
        entity.setVideoUrl(dto.getVideoUrl());
        entity.setPrice(dto.getPrice());
        entity.setOfferTag(dto.getOfferTag());
        entity.setLocation(dto.getLocation());
        entity.setCategory(dto.getCategory());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setMediaUrls(dto.getMediaUrls());
        entity.setShopId(dto.getShopId());
        entity.setSellerId(dto.getSellerId());

        return entity;
    }
}
