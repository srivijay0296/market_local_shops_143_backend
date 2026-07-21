package com.marketlocalshops.shops;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marketlocalshops.markets.Market;
import com.marketlocalshops.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDTO {
    private Long id;
    
    @jakarta.validation.constraints.NotBlank(message = "Shop name is required")
    private String name;
    
    private String description;
    
    private User owner;
    private Market market;
    
    private String category;
    private String status;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    @JsonProperty("vendor_name")
    private String vendorName;
    
    private String location;
    private String phone;
    
    @JsonProperty("owner_id")
    private Long ownerId;
    
    @JsonProperty("market_id")
    private Long marketId;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
