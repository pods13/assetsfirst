package com.topably.assets.auth.service;

import com.topably.assets.auth.domain.ChangePasswordDto;
import com.topably.assets.auth.domain.CreateUserDto;
import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.domain.UserDto;
import com.topably.assets.auth.domain.role.Roles;
import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.auth.event.UserCreatedEvent;
import com.topably.assets.auth.repository.AuthorityRepository;
import com.topably.assets.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserDto createNewUserAccount(CreateUserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException();
        }
        var userRole = authorityRepository.findByRole(Roles.USER.name())
                .orElseThrow(() -> new RuntimeException("User Role Not Found"));
        var firstLogin = true;
        User user = new User()
            .setUsername(userDto.getUsername())
            .setPassword(passwordEncoder.encode(userDto.getPassword()))
            .setAuthorities(Set.of(userRole))
            .setAccountNonLocked(true)
            .setAccountNonExpired(true)
            .setCredentialsNonExpired(true)
            .setEnabled(true)
            .setFirstLogin(firstLogin);
        User newUser = userRepository.save(user);
        eventPublisher.publishEvent(new UserCreatedEvent(this, newUser.getId(), userDto.isProvideData()));
        return new UserDto(user.getId(), user.getUsername(), firstLogin);
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
            .get();
        return pwdGenerator.generate(8);
    }

    public UserDto changePassword(CurrentUser currentUser, ChangePasswordDto dto) {
        var user = userRepository.findById(currentUser.getUserId()).orElseThrow();
        var firstLogin = Boolean.TRUE.equals(user.getFirstLogin());
        if (firstLogin || hasSameCurrentPassword(user, dto.currentPassword())) {
            if (firstLogin) {
                user.setFirstLogin(false);
            }
            if (!Objects.equals(dto.newPassword(), dto.confirmNewPassword())) {
                throw new RuntimeException("New password and confirm password have to be the same");
            }
            user.setPassword(passwordEncoder.encode(dto.newPassword()));
        } else {
            throw new RuntimeException("Password is incorrect");
        }
        return new UserDto(user.getId(), user.getUsername(), user.getFirstLogin());
    }

    private boolean hasSameCurrentPassword(User user, String currentPassword) {
        return currentPassword != null && passwordEncoder.matches(currentPassword, user.getPassword());
    }
}
