package com.marketlocalshops.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marketlocalshops.shops.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "Product name is required")
    private String name;

    @jakarta.validation.constraints.NotNull(message = "Product price is required")
    private Double price;
    private String description;
    private String category;
    private Integer stock;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    @JsonProperty("is_approved")
    private Boolean isApproved;
    
    @JsonProperty("view_count")
    private Integer viewCount;
    
    @JsonProperty("shop_id")
    private Long shopId;
    
    private Shop shop;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
