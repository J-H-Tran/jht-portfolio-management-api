package com.pgim.portfolio.domain;

import com.pgim.portfolio.domain.dto.pUser.PUserDTO;
import com.pgim.portfolio.domain.dto.pUser.UserRegistrationDTO;
import com.pgim.portfolio.domain.entity.pm.PUser;
import com.pgim.portfolio.domain.entity.pm.PRole;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * UserMapper for converting between User entity and DTOs.
 *
 * Security Note:
 * - Password is NEVER mapped to UserDTO (read operations)
 * - Password from UserRegistrationDTO must be BCrypt hashed before saving
 * - Role names are extracted for DTO representation
 *
 * Example usage:
 *   UserDTO dto = userMapper.toDTO(user);
 *   User entity = userMapper.toEntity(registrationDTO);
 */
@Mapper(componentModel = "spring")
public interface PUserMapper {

    /**
     * Maps User entity to UserDTO.
     * Extracts role names from Role entities.
     */
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRoleNames")
    PUserDTO toDTO(PUser pUser);

    /**
     * Maps UserRegistrationDTO to User entity.
     * Note: Password must be hashed in service layer before saving.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    PUser toEntity(UserRegistrationDTO registrationDTO);

    /**
     * Custom mapping to extract role names from Role entities.
     */
    @Named("rolesToRoleNames")
    default Set<String> rolesToRoleNames(Set<PRole> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .map(PRole::getName)
                .collect(Collectors.toSet());
    }
}