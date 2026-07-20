package com.marketlocalshops.markets;

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
public class MarketDTO {
    private Long id;
    private String name;
    private String location;
    private String status;
    private String slug;
    private String description;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
