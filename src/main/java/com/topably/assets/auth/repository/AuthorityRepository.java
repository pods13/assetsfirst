package com.topably.assets.auth.repository;

import com.topably.assets.auth.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByRole(String roleName);
}
