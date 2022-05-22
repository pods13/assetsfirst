package com.topably.assets.auth.service;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.domain.UserDto;
import com.topably.assets.auth.event.UserCreatedEvent;
import com.topably.assets.auth.repository.AuthorityRepository;
import com.topably.assets.auth.repository.UserRepository;
import com.topably.assets.portfolios.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public User getById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional
    public User createNewUserAccount(UserDto userDto) {
        var userRole = authorityRepository.findByRole("USER");
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .authority(userRole)
                .build();
        User newUser = userRepository.save(user);
        eventPublisher.publishEvent(new UserCreatedEvent(this, newUser.getId()));
        return newUser;
    }
}
