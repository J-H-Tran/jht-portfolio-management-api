package com.pgim.portfolio.repository.pm;

import com.pgim.portfolio.domain.entity.pm.PortfolioUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<PortfolioUser, Long> {
    Optional<PortfolioUser> findByUsername(String username);
}