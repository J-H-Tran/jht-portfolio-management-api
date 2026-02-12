package com.pgim.portfolio.domain.dto.auth;

/**
 * UserResponseDTO for authentication responses.
 * Includes JWT token after successful login.
 */
public record AppUserResponseDTO(
    Long id,
    String username,
    String email,
    String token,
    String tokenType
) {
    public AppUserResponseDTO(
        Long id,
        String username,
        String email,
        String token
    ) {
        this(id, username, email, token, "Bearer");
    }
}