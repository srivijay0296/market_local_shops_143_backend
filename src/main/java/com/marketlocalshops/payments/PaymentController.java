package com.marketlocalshops.payments;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<Payment> createPaymentOrder(@RequestBody Map<String, Object> payload) {
        Long orderId = payload.get("orderId") != null ? Long.valueOf(payload.get("orderId").toString()) : 1L;
        Double amount = payload.get("amount") != null ? Double.valueOf(payload.get("amount").toString()) : null;
        String gateway = payload.get("gateway") != null ? payload.get("gateway").toString() : "COD";
        Long userId = payload.get("userId") != null ? Long.valueOf(payload.get("userId").toString()) : null;

        Payment payment = paymentService.createPaymentOrder(orderId, amount, gateway, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/verify")
    public ResponseEntity<Payment> verifyPayment(@RequestBody Map<String, String> payload) {
        String providerOrderId = payload.get("providerOrderId");
        String providerPaymentId = payload.get("providerPaymentId");
        String paymentSignature = payload.get("paymentSignature");

        Payment payment = paymentService.verifyPayment(providerOrderId, providerPaymentId, paymentSignature);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(paymentService.processWebhook(payload));
    }

    @PostMapping("/refund")
    public ResponseEntity<Payment> refundPayment(@RequestBody Map<String, Object> payload) {
        Long paymentId = Long.valueOf(payload.get("paymentId").toString());
        return ResponseEntity.ok(paymentService.refundPayment(paymentId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getPaymentHistory(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<String> downloadInvoice(@PathVariable Long id) {
        String htmlInvoice = paymentService.generateInvoiceHtml(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.setContentDispositionFormData("attachment", "invoice-" + id + ".html");
        return new ResponseEntity<>(htmlInvoice, headers, HttpStatus.OK);
    }
}
