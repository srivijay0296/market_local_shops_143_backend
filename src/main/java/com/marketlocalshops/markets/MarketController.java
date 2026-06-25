package com.marketlocalshops.markets;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/markets")
public class MarketController {
    private final MarketRepository marketRepository;

    public MarketController(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    @GetMapping
    public ResponseEntity<List<Market>> getAllMarkets() {
        return ResponseEntity.ok(marketRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Market> createMarket(@RequestBody Market market) {
        return ResponseEntity.ok(marketRepository.save(market));
    }
}
