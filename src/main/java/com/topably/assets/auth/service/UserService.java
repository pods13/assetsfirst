package com.topably.assets.auth.service;

import com.topably.assets.auth.domain.User;

public interface UserService {

    User getById(Long id);

    User findByUsername(String username);
}
