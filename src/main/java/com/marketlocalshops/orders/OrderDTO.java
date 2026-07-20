package com.marketlocalshops.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marketlocalshops.users.User;
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
public class OrderDTO {
    private Long id;
    
    private User user;
    
    private Double totalAmount;
    private String status;
    
    @JsonProperty("shipping_address")
    private String shippingAddress;
    
    @JsonProperty("customer_name")
    private String customerName;
    
    @JsonProperty("customer_phone")
    private String customerPhone;
    
    @JsonProperty("customer_email")
    private String customerEmail;
    
    @JsonProperty("user_id")
    private Long userId;
    
    private List<OrderItem> items;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
