package com.marketlocalshops.shops;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRequest {

    @NotBlank(message = "Shop name is required")
    private String name;

    private String description;
    private String category;

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
}
