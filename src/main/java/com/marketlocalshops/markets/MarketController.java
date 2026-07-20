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
            try {
                return ResponseEntity.ok(marketService.getMarketById(id));
            } catch (com.marketlocalshops.exception.ResourceNotFoundException e) {
                return ResponseEntity.ok(null);
            }
        }
        if (slug != null) {
            try {
                return ResponseEntity.ok(marketService.getMarketBySlug(slug));
            } catch (com.marketlocalshops.exception.ResourceNotFoundException e) {
                return ResponseEntity.ok(null);
            }
        }

        if (page != null && size != null) {
            String[] sortParts = sort.split(",");
            Sort.Direction direction = (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1])) 
                    ? Sort.Direction.ASC 
                    : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
            
            return ResponseEntity.ok(marketService.findMarketsWithFilters(search, pageable));
        }

        // Backward compatibility fallback: return a simple List
        return ResponseEntity.ok(marketService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarketDTO> createMarket(@RequestBody MarketDTO marketDTO) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(marketService.createMarket(marketDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarketDTO> updateMarket(@PathVariable Long id, @RequestBody MarketDTO updates) {
        return ResponseEntity.ok(marketService.updateMarket(id, updates));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMarket(@PathVariable Long id) {
        marketService.deleteMarket(id);
        return ResponseEntity.noContent().build();
    }
}
