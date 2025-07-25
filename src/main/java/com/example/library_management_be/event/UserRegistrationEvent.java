package com.example.library_management_be.event;

import com.example.library_management_be.entity.User;
import org.springframework.context.ApplicationEvent;

public class UserRegistrationEvent extends ApplicationEvent {
    private final User user;

    public UserRegistrationEvent(Object source,User user) {
        super(source);
        this.user = user;
    }
    public User getUser() {
        return user;
    }
}
