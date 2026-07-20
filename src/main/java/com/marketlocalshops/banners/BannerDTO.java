package com.marketlocalshops.banners;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDTO {
    private Long id;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    private Boolean active;
    
    @JsonProperty("sort_order")
    private Integer sortOrder;
    
    private String title;
    private String description;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
