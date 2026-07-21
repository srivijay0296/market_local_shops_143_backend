package com.marketlocalshops.payments;

import com.marketlocalshops.exception.BadRequestException;
import com.marketlocalshops.exception.ResourceNotFoundException;
import com.marketlocalshops.orders.Order;
import com.marketlocalshops.orders.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment createPaymentOrder(Long orderId, Double amount, String gateway, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        double paymentAmount = (amount != null && amount > 0) ? amount : (order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);
        String selectedGateway = (gateway != null && !gateway.isBlank()) ? gateway.toUpperCase() : "COD";

        String providerOrderId = "order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .userId(userId != null ? userId : (order.getUser() != null ? order.getUser().getId() : 1L))
                .amount(paymentAmount)
                .currency("INR")
                .gateway(selectedGateway)
                .providerOrderId(providerOrderId)
                .status("COD".equals(selectedGateway) ? "SUCCESS" : "PENDING")
                .invoiceNumber("INV-" + System.currentTimeMillis())
                .build();

        if ("COD".equals(selectedGateway)) {
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment verifyPayment(String providerOrderId, String providerPaymentId, String paymentSignature) {
        Payment payment = paymentRepository.findByProviderOrderId(providerOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for provider order: " + providerOrderId));

        payment.setProviderPaymentId(providerPaymentId != null ? providerPaymentId : "pay_" + UUID.randomUUID().toString().substring(0, 10));
        payment.setPaymentSignature(paymentSignature != null ? paymentSignature : "sig_" + UUID.randomUUID().toString().substring(0, 10));
        payment.setStatus("SUCCESS");

        Order order = orderRepository.findById(payment.getOrderId()).orElse(null);
        if (order != null) {
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public Map<String, String> processWebhook(Map<String, Object> payload) {
        Map<String, String> result = new HashMap<>();
        result.put("status", "processed");
        result.put("message", "Webhook received successfully");
        return result;
    }

    @Transactional
    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found: " + paymentId));

        payment.setStatus("REFUNDED");

        Order order = orderRepository.findById(payment.getOrderId()).orElse(null);
        if (order != null) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
        }

        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentHistory(Long userId) {
        if (userId != null) {
            return paymentRepository.findByUserId(userId);
        }
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public String generateInvoiceHtml(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        Order order = orderRepository.findById(payment.getOrderId()).orElse(null);

        String customerName = order != null && order.getCustomerName() != null ? order.getCustomerName() : "Valued Customer";
        String shippingAddress = order != null && order.getShippingAddress() != null ? order.getShippingAddress() : "Address N/A";
        double total = payment.getAmount();
        double gst = total * 0.18;
        double grandTotal = total + gst;

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: 'Helvetica Neue', Arial, sans-serif; color: #333; margin: 40px; }
                .invoice-box { border: 1px solid #eee; padding: 30px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
                .header { display: flex; justify-content: space-between; border-bottom: 2px solid #3b82f6; padding-bottom: 15px; }
                .title { font-size: 24px; font-weight: bold; color: #1e3a8a; }
                .table { width: 100%; border-collapse: collapse; margin-top: 25px; }
                .table th, .table td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                .table th { background: #f8fafc; }
                .total { text-align: right; margin-top: 20px; font-size: 18px; font-weight: bold; }
            </style>
        </head>
        <body>
            <div class="invoice-box">
                <div class="header">
                    <div>
                        <div class="title">Namma Market - Tax Invoice</div>
                        <div>Invoice No: %s</div>
                        <div>Date: %s</div>
                    </div>
                    <div style="text-align:right;">
                        <div>Status: <span style="color:green;font-weight:bold;">%s</span></div>
                        <div>Gateway: %s</div>
                    </div>
                </div>
                <div style="margin-top:20px;">
                    <strong>Billed To:</strong> %s<br/>
                    <strong>Address:</strong> %s
                </div>
                <table class="table">
                    <thead>
                        <tr><th>Description</th><th>Subtotal</th><th>GST (18%%)</th><th>Total</th></tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Order #%d Items</td>
                            <td>₹%.2f</td>
                            <td>₹%.2f</td>
                            <td>₹%.2f</td>
                        </tr>
                    </tbody>
                </table>
                <div class="total">Grand Total: ₹%.2f</div>
            </div>
        </body>
        </html>
        """.formatted(
                payment.getInvoiceNumber(),
                payment.getCreatedAt() != null ? payment.getCreatedAt().toString() : "N/A",
                payment.getStatus(),
                payment.getGateway(),
                customerName,
                shippingAddress,
                payment.getOrderId(),
                total,
                gst,
                grandTotal,
                grandTotal
        );
    }
}
