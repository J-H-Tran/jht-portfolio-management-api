package com.pgim.portfolio.domain.dto.pUser;

/**
 * UserResponseDTO for authentication responses.
 * Includes JWT token after successful login.
 */
public record UserResponseDTO(
    Long id,
    String username,
    String email,
    String token,
    String tokenType
) {
    public UserResponseDTO(
        Long id,
        String username,
        String email,
        String token
    ) {
        this(id, username, email, token, "Bearer");
    }
}