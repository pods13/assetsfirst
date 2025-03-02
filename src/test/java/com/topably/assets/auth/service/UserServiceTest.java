package com.topably.assets.auth.service;

import com.topably.assets.auth.domain.CreateUserDto;
import com.topably.assets.auth.domain.UserDto;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@IT
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserServiceTest extends IntegrationTestBase {

    @Autowired
    private UserService userService;

    @Test
    public void testCreationOfNewUser() {
        var expectedUserName = "testusername";
        var actualUser = userService.createNewUserAccount(
            new CreateUserDto().setUsername(expectedUserName)
                .setPassword("password")
        );
        assertThat(actualUser).extracting(UserDto::id).isNotNull();
        assertThat(actualUser).extracting(UserDto::username).isEqualTo(expectedUserName);
        assertThat(actualUser).extracting(UserDto::firstLogin).isEqualTo(true);
    }

    @Test
    public void testGenerateAnonymousUser() {
        var result = userService.generateAnonymousUser();
        assertThat(result).extracting(CreateUserDto::getUsername).isNotNull();
        assertThat(result).extracting(CreateUserDto::getPassword).isNotNull();
        assertThat(result).extracting(CreateUserDto::isProvideData).isEqualTo(true);

    }

    @Test
    public void testDuplicateUsernameThrowsException() {
        var username = "duplicateuser";
        var createUserDto = new CreateUserDto()
            .setUsername(username)
            .setPassword("password");

        userService.createNewUserAccount(createUserDto);

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            userService.createNewUserAccount(createUserDto);
        });
    }

    @Test
    public void testChangePasswordFirstLogin() {
        // Create new user (will have firstLogin=true)
        var createUserDto = new CreateUserDto()
            .setUsername("firstloginuser")
            .setPassword("initial");
        var createdUser = userService.createNewUserAccount(createUserDto);

        // Change password on first login (current password not required)
        var currentUser = new com.topably.assets.auth.domain.security.CurrentUser(
            userService.getById(createdUser.id()),
            java.util.Collections.emptyList()
        );
        var changePasswordDto = new com.topably.assets.auth.domain.ChangePasswordDto(
            null, // Current password not needed for first login
            "newpassword",
            "newpassword"
        );

        var result = userService.changePassword(currentUser, changePasswordDto);
        assertThat(result.firstLogin()).isFalse();
    }

    @Test
    public void testChangePasswordValidationFailures() {
        // Create and setup user
        var createUserDto = new CreateUserDto()
            .setUsername("validationuser")
            .setPassword("initial");
        var createdUser = userService.createNewUserAccount(createUserDto);

        var currentUser = new com.topably.assets.auth.domain.security.CurrentUser(
            userService.getById(createdUser.id()),
            java.util.Collections.emptyList()
        );

        // Make it a regular user first
        userService.changePassword(currentUser, new com.topably.assets.auth.domain.ChangePasswordDto(
            null, "password1", "password1"
        ));

        // Test wrong current password
        var wrongCurrentPassword = new com.topably.assets.auth.domain.ChangePasswordDto(
            "wrongpassword",
            "newpassword",
            "newpassword"
        );
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            userService.changePassword(currentUser, wrongCurrentPassword);
        });

        // Test mismatched new passwords
        var mismatchedPasswords = new com.topably.assets.auth.domain.ChangePasswordDto(
            "password1",
            "newpassword1",
            "newpassword2"
        );
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            userService.changePassword(currentUser, mismatchedPasswords);
        });
    }
}
