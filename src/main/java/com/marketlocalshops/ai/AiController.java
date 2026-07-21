package com.marketlocalshops.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate-description")
    public ResponseEntity<Map<String, Object>> generateDescription(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String category = (String) body.get("category");
        List<String> features = body.get("features") instanceof List ? (List<String>) body.get("features") : Collections.emptyList();

        return ResponseEntity.ok(aiService.generateProductDescription(name, category, features));
    }

    @PostMapping("/marketing-post")
    public ResponseEntity<Map<String, Object>> generateMarketingPost(@RequestBody Map<String, Object> body) {
        String shopName = (String) body.get("shopName");
        String productName = (String) body.get("productName");
        Double discount = body.get("discountPercent") != null ? Double.valueOf(body.get("discountPercent").toString()) : null;

        return ResponseEntity.ok(aiService.generateMarketingPost(shopName, productName, discount));
    }

    @GetMapping("/seller-insights")
    public ResponseEntity<Map<String, Object>> getSellerInsights(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Double currentPrice,
            @RequestParam(required = false) Integer currentStock) {
        return ResponseEntity.ok(aiService.getSellerAiInsights(shopId, currentPrice, currentStock));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Map<String, Object>>> getRecommendations(
            @RequestParam(required = false) String preferences,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(aiService.getSmartRecommendations(preferences, category));
    }
}
