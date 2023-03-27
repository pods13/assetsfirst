package com.topably.assets.auth;

import com.topably.assets.auth.domain.ChangePasswordDto;
import com.topably.assets.auth.domain.UserDto;
import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @PostMapping("/change-password")
    public UserDto changePassword(@AuthenticationPrincipal CurrentUser user, @RequestBody @Validated ChangePasswordDto dto) {
        return userService.changePassword(user, dto);
    }
}
