package com.marketlocalshops.shops;

import com.marketlocalshops.users.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "shops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private com.marketlocalshops.markets.Market market;

    private String category;

    @Column(nullable = false)
    private String status = "pending";

    @Column(name = "image_url")
    @com.fasterxml.jackson.annotation.JsonProperty("image_url")
    private String imageUrl;

    @Column(name = "vendor_name")
    @com.fasterxml.jackson.annotation.JsonProperty("vendor_name")
    private String vendorName;

    private String location;

    private String phone;

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("owner_id")
    private Long ownerId;

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("market_id")
    private Long marketId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "pending";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
