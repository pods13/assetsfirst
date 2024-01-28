package com.topably.assets.auth.listener;

import com.topably.assets.auth.domain.CreateUserDto;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.core.config.demo.DemoDataConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DemoUserOnStartupApplicationListener {

    private final DemoDataConfig demoDataConfig;

    private final UserService userService;

    @EventListener
    public void createDemoUserOnApplicationStartupIfNotExist(ContextRefreshedEvent event) {
        userService.findByUsername(demoDataConfig.getUsername())
            .ifPresentOrElse(user -> {}, () -> {
                userService.createNewUserAccount(new CreateUserDto()
                    .setUsername(demoDataConfig.getUsername())
                    .setPassword(demoDataConfig.getUsername())
                    .setProvideData(true));
            });
    }

}
