package com.pgim.portfolio.repository.pm;

import com.pgim.portfolio.domain.entity.pm.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser getByUsername(String username);
}