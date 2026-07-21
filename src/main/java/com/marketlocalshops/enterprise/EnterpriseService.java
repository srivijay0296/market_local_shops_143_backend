package com.marketlocalshops.enterprise;

import com.marketlocalshops.exception.BadRequestException;
import com.marketlocalshops.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    public Map<String, Object> validateCoupon(String code, Double orderAmount) {
        if (code == null || code.isBlank()) {
            throw new BadRequestException("Coupon code is required");
        }

        double amount = orderAmount != null ? orderAmount : 0.0;
        double discount = 0.0;

        if ("WELCOME50".equalsIgnoreCase(code.trim())) {
            discount = Math.min(amount * 0.20, 150.0);
        } else if ("FESTIVE100".equalsIgnoreCase(code.trim())) {
            discount = amount >= 500 ? 100.0 : 0.0;
        } else {
            discount = amount * 0.10;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", code.toUpperCase());
        response.put("isValid", true);
        response.put("discountAmount", discount);
        response.put("finalAmount", Math.max(0.0, amount - discount));
        return response;
    }

    public Map<String, Object> updateDeliveryGps(Long partnerId, Double lat, Double lng) {
        Map<String, Object> location = new HashMap<>();
        location.put("partnerId", partnerId);
        location.put("latitude", lat != null ? lat : 12.9716);
        location.put("longitude", lng != null ? lng : 77.5946);
        location.put("updatedAt", new Date());
        location.put("status", "ACTIVE_TRACKING");
        return location;
    }

    public Map<String, Object> getTenantBranding(String domain) {
        String host = domain != null ? domain : "nammamarket.com";

        Map<String, Object> branding = new HashMap<>();
        branding.put("tenantId", "tenant_" + Math.abs(host.hashCode()));
        branding.put("customDomain", host);
        branding.put("siteName", "Namma Market Enterprise");
        branding.put("primaryColor", "#2563eb");
        branding.put("secondaryColor", "#0f172a");
        branding.put("logoUrl", "https://nammamarket.com/assets/logo.png");
        branding.put("enableWhiteLabel", true);
        return branding;
    }

    public Map<String, Object> calculateVendorPayout(Long shopId, Double grossSales, Double commissionRate) {
        double sales = grossSales != null ? grossSales : 0.0;
        double rate = commissionRate != null ? commissionRate : 0.05; // 5% platform fee
        double commissionFee = sales * rate;
        double netPayout = sales - commissionFee;

        Map<String, Object> payout = new HashMap<>();
        payout.put("shopId", shopId);
        payout.put("grossSales", sales);
        payout.put("commissionFee", commissionFee);
        payout.put("netPayout", netPayout);
        payout.put("payoutStatus", "READY_FOR_DISBURSAL");
        return payout;
    }
}
