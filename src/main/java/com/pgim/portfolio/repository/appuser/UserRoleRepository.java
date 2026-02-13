package com.pgim.portfolio.repository.appuser;

import com.pgim.portfolio.domain.entity.appuser.AppUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<AppUserRole, Long> {
    Set<AppUserRole> findByUserId(Long id);
}