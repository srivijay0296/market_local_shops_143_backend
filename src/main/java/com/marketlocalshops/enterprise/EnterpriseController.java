package com.marketlocalshops.enterprise;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/enterprise")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    @PostMapping("/coupons/validate")
    public ResponseEntity<Map<String, Object>> validateCoupon(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        Double amount = body.get("orderAmount") != null ? Double.valueOf(body.get("orderAmount").toString()) : 0.0;
        return ResponseEntity.ok(enterpriseService.validateCoupon(code, amount));
    }

    @PostMapping("/delivery/gps")
    public ResponseEntity<Map<String, Object>> updateGps(@RequestBody Map<String, Object> body) {
        Long partnerId = body.get("partnerId") != null ? Long.valueOf(body.get("partnerId").toString()) : 1L;
        Double lat = body.get("latitude") != null ? Double.valueOf(body.get("latitude").toString()) : 12.9716;
        Double lng = body.get("longitude") != null ? Double.valueOf(body.get("longitude").toString()) : 77.5946;
        return ResponseEntity.ok(enterpriseService.updateDeliveryGps(partnerId, lat, lng));
    }

    @GetMapping("/tenants/branding")
    public ResponseEntity<Map<String, Object>> getTenantBranding(@RequestParam(required = false) String domain) {
        return ResponseEntity.ok(enterpriseService.getTenantBranding(domain));
    }

    @PostMapping("/payouts/calculate")
    public ResponseEntity<Map<String, Object>> calculatePayout(@RequestBody Map<String, Object> body) {
        Long shopId = body.get("shopId") != null ? Long.valueOf(body.get("shopId").toString()) : 1L;
        Double grossSales = body.get("grossSales") != null ? Double.valueOf(body.get("grossSales").toString()) : 0.0;
        Double commissionRate = body.get("commissionRate") != null ? Double.valueOf(body.get("commissionRate").toString()) : 0.05;
        return ResponseEntity.ok(enterpriseService.calculateVendorPayout(shopId, grossSales, commissionRate));
    }
}
