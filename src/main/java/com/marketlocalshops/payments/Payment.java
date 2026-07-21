package com.marketlocalshops.payments;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String gateway; // RAZORPAY, STRIPE, COD

    @Column(name = "provider_order_id")
    private String providerOrderId;

    @Column(name = "provider_payment_id")
    private String providerPaymentId;

    @Column(name = "payment_signature")
    private String paymentSignature;

    @Column(nullable = false)
    private String status; // PENDING, SUCCESS, FAILED, REFUNDED

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currency == null) currency = "INR";
        if (status == null) status = "PENDING";
        if (invoiceNumber == null) invoiceNumber = "INV-" + System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
