package com.pgim.portfolio.repository.pm;

import com.pgim.portfolio.domain.entity.pm.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
