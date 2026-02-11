package com.pgim.portfolio.api.controller;

import com.pgim.portfolio.domain.dto.pm.LoginDTO;
import com.pgim.portfolio.service.pm.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class CsrfController {
    private UserServiceImpl userService;

    public CsrfController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/csrf")
    public Map<String, String> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return Map.of("csrfToken", csrfToken.getToken());
    }

    @PostMapping("/login")
    public ResponseEntity<String> tryLogin(
            @RequestBody LoginDTO loginDTO) {
        try {
            var userDetails = userService.loadUserByUsername(loginDTO.username());
            return ResponseEntity.noContent().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}