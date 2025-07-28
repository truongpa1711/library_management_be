package com.example.library_management_be.service;

import com.example.library_management_be.entity.Notification;
import com.example.library_management_be.entity.User;
import com.example.library_management_be.entity.enums.ENotificationType;
import com.example.library_management_be.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Async
    public void notifyUser(User user, String title, String content, ENotificationType type) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setUser(user);
        notification.setType(type);
        notification.setIsRead(false);
        notification.setCreatedDate(LocalDateTime.now());
        notificationRepository.save(notification);
        // Gửi realtime qua WebSocket tới user
        messagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), notification);
    }
}
