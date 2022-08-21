package com.topably.assets.core.bootstrap;

import com.topably.assets.auth.domain.CreateUserDto;
import com.topably.assets.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Order(10)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class UserDataLoader implements CommandLineRunner {

    private final UserService userService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        userService.createNewUserAccount(CreateUserDto.builder().username("user").password("&}vU6Nw6").build());
    }
}
