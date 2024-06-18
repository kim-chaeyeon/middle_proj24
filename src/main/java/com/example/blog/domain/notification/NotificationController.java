package com.example.blog.domain.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestParam String senderUsername, @RequestParam String receiverUsername, @RequestParam String message) {
        Notification notification = notificationService.sendNotification(senderUsername, receiverUsername, message);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<Void> acceptNotification(@PathVariable Long id) {
        notificationService.acceptNotification(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<Void> rejectNotification(@PathVariable Long id) {
        notificationService.rejectNotification(id);
        return ResponseEntity.ok().build();
    }
}
