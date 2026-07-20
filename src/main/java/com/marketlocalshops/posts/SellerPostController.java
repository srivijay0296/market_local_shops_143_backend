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
            @RequestParam(name = "seller_id", required = false) Long sellerId,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,desc") String sort) {
        
        if (page != null && size != null) {
            String[] sortParts = sort.split(",");
            Sort.Direction direction = (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1])) 
                    ? Sort.Direction.ASC 
                    : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
            
            return ResponseEntity.ok(sellerPostService.findPostsWithFilters(sellerId, category, search, pageable));
        }

        // Backward compatibility fallback
        if (sellerId != null && category != null) {
            return ResponseEntity.ok(sellerPostService.findByShopIdAndCategory(sellerId, category));
        } else if (sellerId != null) {
            return ResponseEntity.ok(sellerPostService.findByShopId(sellerId));
        } else if (category != null) {
            return ResponseEntity.ok(sellerPostService.findByCategory(category));
        }
        return ResponseEntity.ok(sellerPostService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerPostDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(sellerPostService.getPostById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<SellerPostDTO> createPost(@RequestBody SellerPostDTO postDTO) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(sellerPostService.createPost(postDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<SellerPostDTO> updatePost(@PathVariable Long id, @RequestBody SellerPostDTO updates) {
        return ResponseEntity.ok(sellerPostService.updatePost(id, updates));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        sellerPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
