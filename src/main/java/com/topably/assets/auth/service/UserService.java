package com.topably.assets.auth.service;

import com.topably.assets.auth.domain.CreateUserDto;
import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.domain.UserDto;
import com.topably.assets.auth.event.UserCreatedEvent;
import com.topably.assets.auth.repository.AuthorityRepository;
import com.topably.assets.auth.repository.UserRepository;
import liquibase.repackaged.org.apache.commons.text.CharacterPredicates;
import liquibase.repackaged.org.apache.commons.text.RandomStringGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher eventPublisher;

    public User getById(Long id) {
        return userRepository.getReferenceById(id);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public UserDto createNewUserAccount(CreateUserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException();
        }
        var userRole = authorityRepository.findByRole("USER");
        User user = User.builder()
            .username(userDto.getUsername())
            .password(passwordEncoder.encode(userDto.getPassword()))
            .authority(userRole)
            .build();
        User newUser = userRepository.save(user);
        eventPublisher.publishEvent(new UserCreatedEvent(this, newUser.getId(), userDto.isProvideData()));
        return new UserDto(user.getId(), user.getUsername());
    }

    public CreateUserDto generateAnonymousUser() {
        return new CreateUserDto()
            .setUsername(generateRandomUsername())
            .setPassword(generateRandomSeq())
            .setProvideData(true);
    }

    //TODO look for better algorithm, working under distributed env
    private String generateRandomUsername() {
        return "user-" + generateRandomSeq();
    }

    private String generateRandomSeq() {
        var pwdGenerator = new RandomStringGenerator.Builder()
            .withinRange('0', 'z')
            .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
            .build();
        return pwdGenerator.generate(8);
    }
}
