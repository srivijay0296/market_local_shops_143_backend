package com.marketlocalshops.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String role;
    
    private String name;
    private String phone;
    private String address;
    
    @com.fasterxml.jackson.annotation.JsonProperty("is_approved")
    private Boolean isApproved;
    
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private java.time.LocalDateTime createdAt;
}
