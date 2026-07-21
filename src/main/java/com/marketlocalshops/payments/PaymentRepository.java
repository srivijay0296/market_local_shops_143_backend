package com.marketlocalshops.payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByProviderOrderId(String providerOrderId);
    List<Payment> findByUserId(Long userId);
}
