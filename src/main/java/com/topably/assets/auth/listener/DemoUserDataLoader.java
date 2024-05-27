package com.topably.assets.auth.listener;

import com.topably.assets.auth.domain.CreateUserDto;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.core.config.demo.DemoDataConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Order(60)
@ConditionalOnClass(DemoDataConfig.class)
public class DemoUserDataLoader implements CommandLineRunner {

    private final DemoDataConfig demoDataConfig;

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        userService.findByUsername(demoDataConfig.getUsername())
            .ifPresentOrElse(user -> {}, () -> {
                userService.createNewUserAccount(new CreateUserDto()
                    .setUsername(demoDataConfig.getUsername())
                    .setPassword(demoDataConfig.getUsername())
                    .setProvideData(true));
            });
    }

}
