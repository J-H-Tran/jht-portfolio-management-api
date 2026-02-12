package com.pgim.portfolio.domain.entity.pm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Role entity for authorization.
 * Represents user roles (e.g., USER, ADMIN, MANAGER).
 *
 * 3NF Compliance:
 * - Primary key: id
 * - Unique constraint on name ensures no duplicates
 * - Many-to-Many relationship with User via user_roles join table
 * - Description is directly dependent on id (no transitive dependencies)
 */

@Entity
@Table(name = "roles", uniqueConstraints = {
    @UniqueConstraint(columnNames = "name")
})
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name (e.g., USER, ADMIN, MANAGER).
     * Stored without "ROLE_" prefix; prefix added in getAuthorities().
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * Bi-directional Many-to-Many relationship with User.
     * Mapped by "roles" field in User entity.
     */
    @ManyToMany(mappedBy = "roles")
    private Set<PUser> users = new HashSet<>();

    public PRole(String name, String description) {
        this.name = name;
        this.description = description;
    }
}