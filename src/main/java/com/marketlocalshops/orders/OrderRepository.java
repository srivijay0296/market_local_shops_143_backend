package com.marketlocalshops.orders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Id(Long userId);

    @Query(
        value = "SELECT o FROM Order o LEFT JOIN FETCH o.user WHERE " +
                "(:userId IS NULL OR o.user.id = :userId) AND " +
                "(:status IS NULL OR LOWER(o.status) = LOWER(:status)) AND " +
                "(:search IS NULL OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.shippingAddress) LIKE LOWER(CONCAT('%', :search, '%')))",
        countQuery = "SELECT COUNT(o) FROM Order o WHERE " +
                "(:userId IS NULL OR o.user.id = :userId) AND " +
                "(:status IS NULL OR LOWER(o.status) = LOWER(:status)) AND " +
                "(:search IS NULL OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.shippingAddress) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Order> findOrdersWithFilters(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable);
}
