package com.example.library_management_be.event;

import com.example.library_management_be.service.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ForgotPasswordListener {
    private final EmailService emailService;

    public ForgotPasswordListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    @Async
    public void handleForgotPasswordEvent(ForgotPasswordEvent event) {
        String email = event.getEmail();
        String newPassword = event.getNewPassword();

        String subject = "Password Reset Request";
        String content = "Your password has been reset. Your new password is: " + newPassword +
                         ". Please change it after logging in.";
        emailService.sendEmail(email, subject, content);
    }
}
