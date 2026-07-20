package com.marketlocalshops.orders;

import com.marketlocalshops.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String status;

    @Column(name = "shipping_address")
    @com.fasterxml.jackson.annotation.JsonProperty("shipping_address")
    private String shippingAddress;

    @Column(name = "customer_name")
    @com.fasterxml.jackson.annotation.JsonProperty("customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    @com.fasterxml.jackson.annotation.JsonProperty("customer_phone")
    private String customerPhone;

    @Column(name = "customer_email")
    @com.fasterxml.jackson.annotation.JsonProperty("customer_email")
    private String customerEmail;

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("user_id")
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<OrderItem> items;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
