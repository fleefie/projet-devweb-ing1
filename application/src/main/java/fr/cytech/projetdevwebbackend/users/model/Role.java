package fr.cytech.projetdevwebbackend.users.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a user role in the system's authorization model.
 * <p>
 * Roles define permissions and access levels within the application.
 * Common roles include USER, ADMIN, PENDING, etc.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Role {

    /**
     * Common role types used in the application.
     * These constants help prevent typos when referencing roles.
     */
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_PENDING = "PENDING";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Role name is required")
    @Size(min = 1, max = 50, message = "Role name must be between 1 and 50 characters")
    private String name;

    /**
     * Creates a new role with the specified name.
     *
     * @param name The name of the role
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Creates a new role with the specified ID and name.
     *
     * @param id   The ID of the role
     * @param name The name of the role
     */
    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Checks if this role is an admin role.
     *
     * @return true if this is an admin role, false otherwise
     */
    public boolean isAdmin() {
        return ROLE_ADMIN.equals(this.name);
    }

    /**
     * Checks if this role is a standard user role.
     *
     * @return true if this is a user role, false otherwise
     */
    public boolean isUser() {
        return ROLE_USER.equals(this.name);
    }

    /**
     * Checks if this role is a pending role.
     *
     * @return true if this is a pending role, false otherwise
     */
    public boolean isPending() {
        return ROLE_PENDING.equals(this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Role role = (Role) o;

        // Two roles are equal if they have the same name, regardless of ID
        if (name != null && name.equals(role.name)) {
            return true;
        }

        // If names are not equal or null, fall back to ID comparison
        return id != null && id.equals(role.id);
    }

    @Override
    public int hashCode() {
        // Use name for hashCode if available (more often used for comparisons)
        return name != null ? name.hashCode() : 0;
    }
}
