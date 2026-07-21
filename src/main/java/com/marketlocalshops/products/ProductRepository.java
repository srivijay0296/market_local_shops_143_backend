package com.marketlocalshops.products;

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
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByShop_Id(Long shopId);
    List<Product> findByCategory(String category);
    List<Product> findByShop_IdAndCategory(Long shopId, String category);

    @Override
    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    Optional<Product> findById(Long id);

    @Query(
        value = "SELECT p FROM Product p LEFT JOIN FETCH p.shop s WHERE " +
                "(:shopId IS NULL OR (s IS NOT NULL AND s.id = :shopId)) AND " +
                "(:category IS NULL OR (p.category IS NOT NULL AND LOWER(p.category) = LOWER(:category))) AND " +
                "(:isApproved IS NULL OR p.isApproved = :isApproved) AND " +
                "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR (p.description IS NOT NULL AND LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))))",
        countQuery = "SELECT COUNT(p) FROM Product p LEFT JOIN p.shop s WHERE " +
                "(:shopId IS NULL OR (s IS NOT NULL AND s.id = :shopId)) AND " +
                "(:category IS NULL OR (p.category IS NOT NULL AND LOWER(p.category) = LOWER(:category))) AND " +
                "(:isApproved IS NULL OR p.isApproved = :isApproved) AND " +
                "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR (p.description IS NOT NULL AND LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))))"
    )
    Page<Product> findProductsWithFilters(
            @Param("shopId") Long shopId,
            @Param("category") String category,
            @Param("isApproved") Boolean isApproved,
            @Param("search") String search,
            Pageable pageable);
}
