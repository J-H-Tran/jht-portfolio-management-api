package com.pgim.portfolio.repository.pm;

import com.pgim.portfolio.domain.entity.pm.PUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<PUser, Long> {
    Optional<PUser> findByUsername(String username);
}