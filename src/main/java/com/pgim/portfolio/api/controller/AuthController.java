package com.pgim.portfolio.api.controller;

import com.pgim.portfolio.domain.dto.auth.LoginDTO;
import com.pgim.portfolio.domain.dto.auth.LoginResponseDTO;
import com.pgim.portfolio.domain.dto.auth.RegistrationDTO;
import com.pgim.portfolio.domain.entity.pm.AppUser;
import com.pgim.portfolio.domain.entity.pm.AppUser.Role;
import com.pgim.portfolio.repository.pm.UserRepository;
import com.pgim.portfolio.service.jwt.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    /**
     * Login endpoint: Authenticate user and return JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

        // Generate JWT
        String jwt = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponseDTO(jwt));
    }

    /**
     * Register endpoint: Create new user.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationDTO request) {
        // Check if user already exists
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // Create new user
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password())); // Hash password
        user.setRoles(Set.of(Role.USER)); // Default role

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Logout endpoint: In JWT, logout is handled client-side by removing token.
     * For server-side logout, you can implement token blacklisting.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Client should remove JWT from storage
        // Optional: Add token to blacklist (Redis, DB)
        return ResponseEntity.ok("Logged out successfully");
    }
}
