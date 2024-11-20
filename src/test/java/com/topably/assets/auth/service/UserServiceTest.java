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
}
