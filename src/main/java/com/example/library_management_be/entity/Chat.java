package com.example.library_management_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatName;

    private Boolean isGroup;
    private String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @ManyToOne
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_users",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;


    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Message> messages = new HashSet<>();

    @Transient
    public String getLastMessageContent() {
        return lastMessage != null ? lastMessage.getContent() : null;
    }

    @Transient
    public LocalDateTime getLastMessageTime() {
        return lastMessage != null ? lastMessage.getTimeStamp() : null;
    }

    @Transient
    public Long getLastMessageSenderId() {
        return lastMessage != null && lastMessage.getUser() != null ? lastMessage.getUser().getId() : null;
    }
}
