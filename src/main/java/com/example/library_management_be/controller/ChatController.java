package com.example.library_management_be.controller;

import com.example.library_management_be.dto.request.PublicMessageRequest;
import com.example.library_management_be.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/public") // Gửi từ FE tới /app/public
    public void sendPublicMessage(PublicMessageRequest publicMessageRequest) {
        chatService.sendPublicMessage(publicMessageRequest);
    }


}
