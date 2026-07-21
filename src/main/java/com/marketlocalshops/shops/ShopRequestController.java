package com.marketlocalshops.shops;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop_requests")
public class ShopRequestController {

    private final ShopService shopService;

    public ShopRequestController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<ShopDTO> createShopRequest(@Valid @RequestBody ShopRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shopService.createShopRequest(request));
    }
}
