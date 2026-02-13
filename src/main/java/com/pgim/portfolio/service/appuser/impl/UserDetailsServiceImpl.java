package com.pgim.portfolio.service.appuser.impl;

import com.pgim.portfolio.domain.entity.appuser.AppUser;
import com.pgim.portfolio.domain.entity.appuser.AppUserRole;
import com.pgim.portfolio.repository.appuser.UserRepository;
import com.pgim.portfolio.repository.appuser.UserRoleRepository;
import com.pgim.portfolio.repository.pm.RoleRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public UserDetailsServiceImpl(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Fetch user from DB
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch roles for user from user_roles
        Set<AppUserRole> userRoles = userRoleRepository.findByUserId(user.getId());

        // Set userRoles in AppUser entity
        user.setUserRoles(userRoles);
        return user;
    }
}