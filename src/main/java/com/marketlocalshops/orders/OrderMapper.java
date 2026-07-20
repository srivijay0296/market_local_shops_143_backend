package com.marketlocalshops.orders;

import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDTO toDto(Order entity) {
        if (entity == null) {
            return null;
        }

        return OrderDTO.builder()
                .id(entity.getId())
                .user(entity.getUser())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .shippingAddress(entity.getShippingAddress())
                .customerName(entity.getCustomerName())
                .customerPhone(entity.getCustomerPhone())
                .customerEmail(entity.getCustomerEmail())
                .userId(entity.getUser() != null ? entity.getUser().getId() : entity.getUserId())
                .items(entity.getItems())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        Order entity = new Order();
        entity.setId(dto.getId());
        entity.setUser(dto.getUser());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setStatus(dto.getStatus());
        entity.setShippingAddress(dto.getShippingAddress());
        entity.setCustomerName(dto.getCustomerName());
        entity.setCustomerPhone(dto.getCustomerPhone());
        entity.setCustomerEmail(dto.getCustomerEmail());
        entity.setUserId(dto.getUserId());
        entity.setItems(dto.getItems());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }
}
