package com.example.blog.domain.notification;

import com.example.blog.domain.user.SiteUser;
import com.example.blog.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification sendNotification(String senderUsername, String receiverUsername, String message) {
        SiteUser sender = userRepository.findByusername(senderUsername).orElseThrow(() -> new RuntimeException("Sender not found"));
        SiteUser receiver = userRepository.findByusername(receiverUsername).orElseThrow(() -> new RuntimeException("Receiver not found"));

        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setMessage(message);
        notification.setAccepted(false);

        return notificationRepository.save(notification);
    }

    public void acceptNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(Math.toIntExact(notificationId)).orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setAccepted(true);
        notificationRepository.save(notification);
    }

    public void rejectNotification(Long notificationId) {
        notificationRepository.deleteById(Math.toIntExact(notificationId));
    }
}
