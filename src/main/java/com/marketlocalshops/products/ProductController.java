package com.marketlocalshops.products;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllProducts(
            @RequestParam(name = "shop_id", required = false) Long shopId,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "is_approved", required = false) Boolean isApproved,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,desc") String sort) {
        
        int pageNum = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 20;
        
        String[] sortParts = (sort != null && !sort.isBlank()) ? sort.split(",") : new String[]{"id", "desc"};
        Sort.Direction direction = (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1])) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(direction, sortParts[0]));

        if (page == null && size == null) {
            Page<ProductDTO> productPage = productService.findProductsWithFilters(shopId, category, isApproved, search, pageable);
            return ResponseEntity.ok(productPage.getContent());
        }

        return ResponseEntity.ok(productService.findProductsWithFilters(shopId, category, isApproved, search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@jakarta.validation.Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(productService.createProduct(productDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ProductDTO updates) {
        return ResponseEntity.ok(productService.updateProduct(id, updates));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchProducts(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "shop_id", required = false) Long shopId,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,desc") String sort) {
        
        String searchTerm = (query != null && !query.isBlank()) ? query : q;
        return getAllProducts(shopId, category, null, searchTerm, page, size, sort);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<java.util.List<ProductDTO>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<java.util.List<ProductDTO>> getProductsByShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(productService.findByShopId(shopId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
