package com.example.library_management_be.service;

import com.example.library_management_be.dto.request.PublicMessageRequest;
import com.example.library_management_be.dto.response.PublicMessageResponse;
import com.example.library_management_be.entity.Message;
import com.example.library_management_be.entity.User;
import com.example.library_management_be.exception.UserException;
import com.example.library_management_be.repository.MessageRepository;
import com.example.library_management_be.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;

    public ChatService(UserRepository userRepository, SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
    }

    // Phương thức để gửi tin nhắn công khai
    public void sendPublicMessage(PublicMessageRequest publicMessageRequest) {
        User sender = userRepository.findById(publicMessageRequest.getSenderId())
                .orElseThrow(() -> new UserException.UserNotFoundException("User not found"));

        Message message = Message.builder()
                .chat(null)
                .user(sender)
                .content(publicMessageRequest.getContent())
                .timeStamp(java.time.LocalDateTime.now())
                .build();
        messageRepository.save(message);

        PublicMessageResponse publicMessageResponse = PublicMessageResponse.builder()
                .senderName(sender.getFullName())
                .senderRole(sender.getRole().name())
                .content(publicMessageRequest.getContent())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        messagingTemplate.convertAndSend("/topic/public", publicMessageResponse);
    }
}
