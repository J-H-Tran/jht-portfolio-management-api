package com.pgim.portfolio.domain.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * UserRegistrationDTO for creating new users.
 * Includes password for initial account setup.
 *
 * Security: Password is validated but never stored in plain text.
 * It will be BCrypt hashed before saving to the database.
 */
public record AppUserRegistrationDTO(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
        String username,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,
        String firstName,
        String lastName,
        Set<String> roles
) {}