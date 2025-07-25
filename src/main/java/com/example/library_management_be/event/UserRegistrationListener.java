package com.example.library_management_be.event;

import com.example.library_management_be.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationListener {
    @Value("${app.url.base}")
    private String baseUrl;

    private final EmailService emailService;

    public UserRegistrationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    @Async
    public void handleUserRegistrationEvent(UserRegistrationEvent event) {
        String email = event.getUser().getEmail();

        String verificationLink = baseUrl + "/api/auth/verify?token=" + event.getUser().getVerifiedToken();

        String subject = "Verify your email address";
        String content = "Welcome! Please verify your email by clicking this link: " + verificationLink;

        emailService.sendEmail(email, subject, content);
    }

}
