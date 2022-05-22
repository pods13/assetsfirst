package com.topably.assets.core.bootstrap;

import com.topably.assets.auth.domain.Authority;
import com.topably.assets.auth.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Order(5)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class AuthorityDataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        authorityRepository.save(Authority.builder().role("USER").build());
    }
}
