package com.topably.assets.trades.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TradeChangedEvent extends ApplicationEvent {


    public TradeChangedEvent(Object source) {
        super(source);
    }
}
