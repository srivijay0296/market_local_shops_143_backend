package com.marketlocalshops.shops;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByMarket_Id(Long marketId);
    List<Shop> findByStatus(String status);
    List<Shop> findByMarket_IdAndStatus(Long marketId, String status);

    @Override
    @Cacheable(value = "shops", key = "#id", unless = "#result == null")
    Optional<Shop> findById(Long id);

    @Query(
        value = "SELECT s FROM Shop s LEFT JOIN FETCH s.owner LEFT JOIN FETCH s.market WHERE " +
                "(:marketId IS NULL OR s.market.id = :marketId) AND " +
                "(:status IS NULL OR LOWER(s.status) = LOWER(:status)) AND " +
                "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.vendorName) LIKE LOWER(CONCAT('%', :search, '%')))",
        countQuery = "SELECT COUNT(s) FROM Shop s WHERE " +
                "(:marketId IS NULL OR s.market.id = :marketId) AND " +
                "(:status IS NULL OR LOWER(s.status) = LOWER(:status)) AND " +
                "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.vendorName) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Shop> findShopsWithFilters(
            @Param("marketId") Long marketId,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable);
}
