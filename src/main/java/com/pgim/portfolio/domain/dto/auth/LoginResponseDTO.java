package com.pgim.portfolio.domain.dto.auth;

/**
 * UserResponseDTO for authentication responses.
 * Includes JWT token after successful login.
 */
public record LoginResponseDTO(
    String token,
    String tokenType
) {
    public LoginResponseDTO(
        String token
    ) {
        this(token, "Bearer");
    }
}
