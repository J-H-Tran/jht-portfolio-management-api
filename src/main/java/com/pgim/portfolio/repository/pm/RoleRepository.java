package com.pgim.portfolio.repository.pm;

import com.pgim.portfolio.domain.entity.pm.AuthRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<AuthRole, Long> {
    Optional<AuthRole> findByName(String name);
}