package com.marketlocalshops.markets;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/markets")
public class MarketController {
    
    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping
    public ResponseEntity<Object> getMarkets(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "slug", required = false) String slug,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,desc") String sort) {
        
        if (id != null) {
            return ResponseEntity.ok(marketService.getMarketById(id));
        }
        if (slug != null) {
            return ResponseEntity.ok(marketService.getMarketBySlug(slug));
        }

        int pageNum = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 20;
        
        String[] sortParts = (sort != null && !sort.isBlank()) ? sort.split(",") : new String[]{"id", "desc"};
        Sort.Direction direction = (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1])) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(direction, sortParts[0]));

        if (page == null && size == null) {
            org.springframework.data.domain.Page<MarketDTO> marketPage = marketService.findMarketsWithFilters(search, pageable);
            return ResponseEntity.ok(marketPage.getContent());
        }

        return ResponseEntity.ok(marketService.findMarketsWithFilters(search, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarketDTO> createMarket(@jakarta.validation.Valid @RequestBody MarketDTO marketDTO) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(marketService.createMarket(marketDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarketDTO> updateMarket(@PathVariable Long id, @jakarta.validation.Valid @RequestBody MarketDTO updates) {
        return ResponseEntity.ok(marketService.updateMarket(id, updates));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMarket(@PathVariable Long id) {
        marketService.deleteMarket(id);
        return ResponseEntity.noContent().build();
    }
}
