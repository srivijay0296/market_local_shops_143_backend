package com.marketlocalshops.enterprise;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_partners")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    private String vehicleType; // BIKE, SCOOTER, VAN
    private String vehicleNumber;
    private String status; // AVAILABLE, ON_DELIVERY, OFFLINE

    private Double currentLatitude;
    private Double currentLongitude;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "AVAILABLE";
    }
}
