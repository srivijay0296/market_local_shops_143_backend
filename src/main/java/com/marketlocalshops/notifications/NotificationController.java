package com.marketlocalshops.notifications;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String role) {
        if (userId != null) {
            return ResponseEntity.ok(notificationRepository.findByUserId(userId));
        }
        if (role != null) {
            return ResponseEntity.ok(notificationRepository.findByRole(role.toUpperCase()));
        }
        return ResponseEntity.ok(notificationRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Notification> sendNotification(@RequestBody Notification notification) {
        if (notification.getRole() == null) notification.setRole("CUSTOMER");
        Notification saved = notificationRepository.save(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        return ResponseEntity.ok(notificationRepository.save(notification));
    }
}
