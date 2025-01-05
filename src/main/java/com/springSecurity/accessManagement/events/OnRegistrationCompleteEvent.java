package com.springSecurity.accessManagement.events;


import com.springSecurity.accessManagement.models.entities.User;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private User user;

    public OnRegistrationCompleteEvent(User user) {
        super(user);

        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public OnRegistrationCompleteEvent setUser(User user) {
        this.user = user;
        return this;
    }
}
