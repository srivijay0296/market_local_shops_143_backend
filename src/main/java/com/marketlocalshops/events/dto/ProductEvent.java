package com.marketlocalshops.events.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductEvent extends BaseEvent {
    private Long productId;
    private Long shopId;
    private String name;
    private Double price;
}
