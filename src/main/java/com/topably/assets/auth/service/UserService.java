package com.topably.assets.auth.service;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.domain.UserDto;

public interface UserService {

    User getById(Long id);

    User findByUsername(String username);

    User createNewUserAccount(UserDto userDto);
}
