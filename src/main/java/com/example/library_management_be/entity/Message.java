package com.example.library_management_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // Nếu cần hỗ trợ gửi file thì bỏ comment 2 dòng dưới
//    private String attachmentUrl;
//    private String attachmentType; // IMAGE, VIDEO, DOCUMENT

    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "reply_to_message_id")
    private Message replyTo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
