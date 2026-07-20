package com.marketlocalshops.markets;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
    
    @Cacheable(value = "markets", key = "#slug", unless = "#result == null")
    Optional<Market> findBySlug(String slug);

    @Override
    @Cacheable(value = "markets", key = "#id", unless = "#result == null")
    Optional<Market> findById(Long id);

    @Query(
        value = "SELECT m FROM Market m WHERE " +
                "(:search IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.location) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))",
        countQuery = "SELECT COUNT(m) FROM Market m WHERE " +
                "(:search IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.location) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Market> findMarketsWithFilters(
            @Param("search") String search,
            Pageable pageable);
}
