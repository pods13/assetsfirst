package com.topably.assets.auth.repository;

import com.topably.assets.auth.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByRole(String roleName);
}
