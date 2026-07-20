package com.marketlocalshops.products;

import com.marketlocalshops.shops.Shop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Shop shop;

    private String description;

    private String category;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 10;

    @Column(name = "image_url")
    @com.fasterxml.jackson.annotation.JsonProperty("image_url")
    private String imageUrl;

    @Column(name = "is_approved", nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("is_approved")
    @Builder.Default
    private Boolean isApproved = false;

    @Column(name = "view_count", nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("shop_id")
    private Long shopId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (stock == null) stock = 10;
        if (isApproved == null) isApproved = false;
        if (viewCount == null) viewCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
