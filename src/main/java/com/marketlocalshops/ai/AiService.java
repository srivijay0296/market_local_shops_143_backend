package com.marketlocalshops.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiService {

    public Map<String, Object> generateProductDescription(String name, String category, List<String> features) {
        String baseName = (name != null && !name.isBlank()) ? name : "Premium Product";
        String cat = (category != null && !category.isBlank()) ? category : "General";

        String description = String.format("Discover the incredible quality of %s. Crafted specifically for %s enthusiasts, this product delivers exceptional reliability, modern elegance, and premium performance for everyday use.", baseName, cat);
        String seoTitle = String.format("Buy %s Online - Best Price in %s", baseName, cat);
        List<String> keywords = List.of(baseName.toLowerCase(), cat.toLowerCase(), "best price", "online shopping", "local market", "namma market");
        List<String> tags = List.of("trending", "featured", cat.toLowerCase(), "top-quality");

        Map<String, Object> result = new HashMap<>();
        result.put("description", description);
        result.put("seoTitle", seoTitle);
        result.put("seoKeywords", keywords);
        result.put("tags", tags);
        return result;
    }

    public Map<String, Object> generateMarketingPost(String shopName, String productName, Double discountPercent) {
        String shop = shopName != null ? shopName : "Namma Local Store";
        String product = productName != null ? productName : "Featured Collection";
        double discount = discountPercent != null ? discountPercent : 15.0;

        String postText = String.format("🎉 EXCLUSIVE OFFER from %s! Get up to %.0f%% OFF on our top-selling %s today! Limited stock available. Visit our shop or order online now on Namma Market! 🛍️✨ #NammaMarket #ShopLocal #SpecialOffer", shop, discount, product);
        String whatsappMsg = String.format("Hi! 👋 Special deal from %s: Enjoy %.0f%% off %s today! Order online: https://nammamarket.com/shop", shop, discount, product);
        String emailSubject = String.format("🔥 Special %.0f%% Discount on %s from %s!", discount, product, shop);

        Map<String, Object> response = new HashMap<>();
        response.put("socialPost", postText);
        response.put("whatsappMessage", whatsappMsg);
        response.put("emailSubject", emailSubject);
        return response;
    }

    public Map<String, Object> getSellerAiInsights(Long shopId, Double currentPrice, Integer currentStock) {
        double price = currentPrice != null ? currentPrice : 499.0;
        int stock = currentStock != null ? currentStock : 15;

        double suggestedPrice = Math.round(price * 0.95 * 100.0) / 100.0;
        int predictedDemandNextWeek = Math.max(5, (int)(stock * 1.4));
        boolean restockRecommended = stock < 10;

        Map<String, Object> insights = new HashMap<>();
        insights.put("suggestedPrice", suggestedPrice);
        insights.put("optimalDiscount", "10% off during weekends");
        insights.put("predictedDemandNext7Days", predictedDemandNextWeek);
        insights.put("restockRecommended", restockRecommended);
        insights.put("aiAdvice", restockRecommended ? "Stock is below safety threshold! Reorder items to avoid missed sales." : "Stock levels are optimal for projected demand.");
        return insights;
    }

    public List<Map<String, Object>> getSmartRecommendations(String userPreferences, String currentCategory) {
        List<Map<String, Object>> recommendations = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 101);
        item1.put("name", "Bargur Handloom Silk Cotton Saree");
        item1.put("price", 1850.00);
        item1.put("matchScore", 98);
        item1.put("reason", "Frequently bought together with textiles");

        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 102);
        item2.put("name", "Kaveri Organic Spices Combo");
        item2.put("price", 450.00);
        item2.put("matchScore", 92);
        item2.put("reason", "Popular in your local market area");

        recommendations.add(item1);
        recommendations.add(item2);
        return recommendations;
    }
}
