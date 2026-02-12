package com.pgim.portfolio.domain.dto.pm;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * UserDTO for transferring user data (excluding sensitive information like password).
 * Used for user profile views and API responses.
 *
 * Security Note: Password is NEVER included in this DTO.
 */
public record AppUserDTO(
    Long id,

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    String firstName,
    String lastName,
    Set<String> roles,
    boolean enabled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastLogin
) {}
