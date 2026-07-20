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
    List<SellerPost> findByMediaType(String mediaType);
    List<SellerPost> findByVideoUrlIsNotNull();

    @Query(
        value = "SELECT p FROM SellerPost p LEFT JOIN FETCH p.shop WHERE " +
                "(:shopId IS NULL OR p.shop.id = :shopId) AND " +
                "(:mediaType IS NULL OR LOWER(p.mediaType) = LOWER(:mediaType)) AND " +
                "(:hasVideo IS NULL OR (:hasVideo = true AND p.videoUrl IS NOT NULL) OR (:hasVideo = false AND p.videoUrl IS NULL)) AND " +
                "(:status IS NULL OR LOWER(p.status) = LOWER(:status)) AND " +
                "(:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.location) LIKE LOWER(CONCAT('%', :search, '%')))",
        countQuery = "SELECT COUNT(p) FROM SellerPost p WHERE " +
                "(:shopId IS NULL OR p.shop.id = :shopId) AND " +
                "(:mediaType IS NULL OR LOWER(p.mediaType) = LOWER(:mediaType)) AND " +
                "(:hasVideo IS NULL OR (:hasVideo = true AND p.videoUrl IS NOT NULL) OR (:hasVideo = false AND p.videoUrl IS NULL)) AND " +
                "(:status IS NULL OR LOWER(p.status) = LOWER(:status)) AND " +
                "(:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.location) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<SellerPost> findPostsWithFilters(
            @Param("shopId") Long shopId,
            @Param("mediaType") String mediaType,
            @Param("hasVideo") Boolean hasVideo,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable);
}
