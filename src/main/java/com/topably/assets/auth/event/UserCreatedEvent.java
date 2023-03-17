package com.topably.assets.auth.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserCreatedEvent extends ApplicationEvent {

    private final Long userId;
    private final boolean provideData;

    public UserCreatedEvent(Object source, Long userId, boolean provideData) {
        super(source);
        this.userId = userId;
        this.provideData = provideData;
    }
}
