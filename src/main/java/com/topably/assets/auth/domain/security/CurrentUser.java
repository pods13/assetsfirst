package com.topably.assets.auth.domain.security;

import com.topably.assets.auth.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private Long userId;

    public CurrentUser(User user, Collection<? extends GrantedAuthority> authorities) {
        this(user.getUsername(), user.getPassword(), user.getEnabled(), user.getAccountNonExpired(),
            user.getCredentialsNonExpired(), user.getAccountNonLocked(), authorities);
        this.userId = user.getId();
    }

    private CurrentUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                        boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
}
