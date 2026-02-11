package com.pgim.portfolio.service.pm.impl;

import com.pgim.portfolio.domain.entity.pm.PortfolioUser;
import com.pgim.portfolio.repository.pm.UserRepository;
import com.pgim.portfolio.service.pm.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PortfolioUser portfolioUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return User
                .withUsername(portfolioUser.getUsername())
                .password(portfolioUser.getPassword())
                .roles(portfolioUser.getRoles().toArray(new String[0]))
                .build();
    }
}