package com.marketlocalshops.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marketlocalshops.shops.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerPostDTO {
    private Long id;
    
    private Shop shop;
    
    @jakarta.validation.constraints.NotBlank(message = "Title is required")
    private String title;
    private String description;
    
    @JsonProperty("media_url")
    private String mediaUrl;
    
    @JsonProperty("media_type")
    private String mediaType;
    
    @JsonProperty("video_url")
    private String videoUrl;
    
    private Double price;
    
    @JsonProperty("offer_tag")
    private String offerTag;
    
    private String location;
    private String category;
    private String status;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("media_urls")
    private List<String> mediaUrls;
    
    @JsonProperty("shop_id")
    private Long shopId;
    
    @JsonProperty("seller_id")
    private Long sellerId;
}
