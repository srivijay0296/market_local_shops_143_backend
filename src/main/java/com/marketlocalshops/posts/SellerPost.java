package com.marketlocalshops.posts;

import com.marketlocalshops.shops.Shop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "seller_posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Shop shop;

    private String title;
    
    private String description;

    @Column(name = "media_url")
    @com.fasterxml.jackson.annotation.JsonProperty("media_url")
    private String mediaUrl;

    @Column(name = "media_type")
    @com.fasterxml.jackson.annotation.JsonProperty("media_type")
    private String mediaType;

    @Column(name = "video_url")
    @com.fasterxml.jackson.annotation.JsonProperty("video_url")
    private String videoUrl;

    private Double price;

    @Column(name = "offer_tag")
    @com.fasterxml.jackson.annotation.JsonProperty("offer_tag")
    private String offerTag;

    private String location;
    
    private String category;

    @Builder.Default
    private String status = "approved";

    @Column(name = "created_at", updatable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @com.fasterxml.jackson.annotation.JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "seller_post_media_urls", joinColumns = @JoinColumn(name = "seller_post_id"))
    @Column(name = "media_url")
    @com.fasterxml.jackson.annotation.JsonProperty("media_urls")
    private List<String> mediaUrls;

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("shop_id")
    private Long shopId;

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("seller_id")
    private Long sellerId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "approved";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
