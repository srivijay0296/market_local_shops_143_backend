package com.marketlocalshops.shops;

import com.marketlocalshops.products.ProductDTO;
import com.marketlocalshops.products.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shops")
public class ShopController {
    
    private final ShopService shopService;
    private final ProductService productService;

    public ShopController(ShopService shopService, ProductService productService) {
        this.shopService = shopService;
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllShops(
            @RequestParam(name = "market_id", required = false) Long marketId,
            @RequestParam(name = "status", required = false) String status,
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
            Page<ShopDTO> shopPage = shopService.findShopsWithFilters(marketId, status, search, pageable);
            return ResponseEntity.ok(shopPage.getContent());
        }

        return ResponseEntity.ok(shopService.findShopsWithFilters(marketId, status, search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopDTO> getShopById(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductDTO>> getShopProducts(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findByShopId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<ShopDTO> createShop(@jakarta.validation.Valid @RequestBody ShopDTO shopDTO) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(shopService.createShop(shopDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<ShopDTO> updateShop(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ShopDTO updates) {
        return ResponseEntity.ok(shopService.updateShop(id, updates));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(shopService.updateStatus(id, body));
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShopDTO>> getRequests() {
        return ResponseEntity.ok(shopService.findByStatus("pending"));
    }

    @PostMapping("/requests/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopDTO> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.approveRequest(id));
    }

    @PostMapping("/requests/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopDTO> rejectRequest(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.rejectRequest(id));
    }
}
