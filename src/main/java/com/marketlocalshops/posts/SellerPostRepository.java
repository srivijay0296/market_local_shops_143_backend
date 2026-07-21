package com.marketlocalshops.posts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerPostRepository extends JpaRepository<SellerPost, Long> {
    List<SellerPost> findByShop_Id(Long shopId);
    List<SellerPost> findByCategory(String category);
    List<SellerPost> findByShop_IdAndCategory(Long shopId, String category);

    @Query(
        value = "SELECT p FROM SellerPost p LEFT JOIN FETCH p.shop s WHERE " +
                "(:shopId IS NULL OR (s IS NOT NULL AND s.id = :shopId)) AND " +
                "(:mediaType IS NULL OR (p.mediaType IS NOT NULL AND LOWER(p.mediaType) = LOWER(:mediaType))) AND " +
                "(:category IS NULL OR (p.category IS NOT NULL AND LOWER(p.category) = LOWER(:category))) AND " +
                "(:hasVideo IS NULL OR (:hasVideo = true AND p.videoUrl IS NOT NULL) OR (:hasVideo = false AND p.videoUrl IS NULL)) AND " +
                "(:status IS NULL OR (p.status IS NOT NULL AND LOWER(p.status) = LOWER(:status))) AND " +
                "(:search IS NULL OR (p.title IS NOT NULL AND LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))) OR (p.description IS NOT NULL AND LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) OR (p.location IS NOT NULL AND LOWER(p.location) LIKE LOWER(CONCAT('%', :search, '%'))))",
        countQuery = "SELECT COUNT(p) FROM SellerPost p LEFT JOIN p.shop s WHERE " +
                "(:shopId IS NULL OR (s IS NOT NULL AND s.id = :shopId)) AND " +
                "(:mediaType IS NULL OR (p.mediaType IS NOT NULL AND LOWER(p.mediaType) = LOWER(:mediaType))) AND " +
                "(:category IS NULL OR (p.category IS NOT NULL AND LOWER(p.category) = LOWER(:category))) AND " +
                "(:hasVideo IS NULL OR (:hasVideo = true AND p.videoUrl IS NOT NULL) OR (:hasVideo = false AND p.videoUrl IS NULL)) AND " +
                "(:status IS NULL OR (p.status IS NOT NULL AND LOWER(p.status) = LOWER(:status))) AND " +
                "(:search IS NULL OR (p.title IS NOT NULL AND LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))) OR (p.description IS NOT NULL AND LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) OR (p.location IS NOT NULL AND LOWER(p.location) LIKE LOWER(CONCAT('%', :search, '%'))))"
    )
    Page<SellerPost> findPostsWithFilters(
            @Param("shopId") Long shopId,
            @Param("mediaType") String mediaType,
            @Param("category") String category,
            @Param("status") String status,
            @Param("search") String search,
            @Param("hasVideo") Boolean hasVideo,
            Pageable pageable);
}
