package com.topably.assets.auth;

import com.topably.assets.auth.domain.CreateUserDto;
import com.topably.assets.auth.domain.UserDto;
import com.topably.assets.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;

    @GetMapping("/user")
    public Principal getCurrentUser(Principal principal) {
        return principal;
    }

    @PostMapping("/signup")
    public UserDto createUser(@RequestBody CreateUserDto userDto) {
        return userService.createNewUserAccount(userDto);
    }
}
