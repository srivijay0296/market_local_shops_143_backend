package com.marketlocalshops.posts;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller_posts")
public class SellerPostController {
    
    private final SellerPostService sellerPostService;

    public SellerPostController(SellerPostService sellerPostService) {
        this.sellerPostService = sellerPostService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllPosts(
            @RequestParam(name = "shopId", required = false) Long shopId,
            @RequestParam(name = "seller_id", required = false) Long sellerId,
            @RequestParam(name = "mediaType", required = false) String mediaType,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "hasVideo", required = false) Boolean hasVideo,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,desc") String sort) {
        
        Long targetShopId = shopId != null ? shopId : sellerId;

        int pageNum = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 20;
        
        String[] sortParts = (sort != null && !sort.isBlank()) ? sort.split(",") : new String[]{"id", "desc"};
        Sort.Direction direction = (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1])) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(direction, sortParts[0]));

        if (page == null && size == null) {
            org.springframework.data.domain.Page<SellerPostDTO> postPage = sellerPostService.findPostsWithFilters(
                    targetShopId, mediaType, category, status, search, hasVideo, pageable);
            return ResponseEntity.ok(postPage.getContent());
        }

        return ResponseEntity.ok(sellerPostService.findPostsWithFilters(
                targetShopId, mediaType, category, status, search, hasVideo, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerPostDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(sellerPostService.getPostById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<SellerPostDTO> createPost(@jakarta.validation.Valid @RequestBody SellerPostDTO postDTO) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(sellerPostService.createPost(postDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<SellerPostDTO> updatePost(@PathVariable Long id, @jakarta.validation.Valid @RequestBody SellerPostDTO updates) {
        return ResponseEntity.ok(sellerPostService.updatePost(id, updates));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        sellerPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
