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

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i JOIN i.product p WHERE p.shop.id = :shopId")
    List<Order> findByShop_Id(@Param("shopId") Long shopId);

    @Query(
        value = "SELECT o FROM Order o LEFT JOIN FETCH o.user u WHERE " +
                "(:userId IS NULL OR (u IS NOT NULL AND u.id = :userId)) AND " +
                "(:status IS NULL OR (o.status IS NOT NULL AND LOWER(o.status) = LOWER(:status))) AND " +
                "(:search IS NULL OR (o.customerName IS NOT NULL AND LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%'))) OR (o.customerEmail IS NOT NULL AND LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%'))) OR (o.shippingAddress IS NOT NULL AND LOWER(o.shippingAddress) LIKE LOWER(CONCAT('%', :search, '%'))))",
        countQuery = "SELECT COUNT(o) FROM Order o LEFT JOIN o.user u WHERE " +
                "(:userId IS NULL OR (u IS NOT NULL AND u.id = :userId)) AND " +
                "(:status IS NULL OR (o.status IS NOT NULL AND LOWER(o.status) = LOWER(:status))) AND " +
                "(:search IS NULL OR (o.customerName IS NOT NULL AND LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%'))) OR (o.customerEmail IS NOT NULL AND LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%'))) OR (o.shippingAddress IS NOT NULL AND LOWER(o.shippingAddress) LIKE LOWER(CONCAT('%', :search, '%'))))"
    )
    Page<Order> findOrdersWithFilters(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable);
}
