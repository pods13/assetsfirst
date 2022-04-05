package com.topably.assets.core.bootstrap;

import com.topably.assets.auth.domain.Authority;
import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.repository.AuthorityRepository;
import com.topably.assets.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Order(1)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class UserDataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var userRole = authorityRepository.save(Authority.builder().role("USER").build());
        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("&}vU6Nw6"))
                .authority(userRole)
                .build());
    }
}
