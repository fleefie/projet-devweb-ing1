package fr.cytech.projetdevwebbackend.users.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cytech.projetdevwebbackend.users.model.Role;

/**
 * Repository interface for Role entity operations.
 * <p>
 * Provides database access methods for role management.
 *
 * @author fleefie
 * @since 2025-03-15
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its name.
     *
     * @param name The name of the role to find
     * @return An Optional containing the role if found, or empty if not found
     */
    Optional<Role> findByName(String name);

    /**
     * Checks if a role exists with the given name.
     *
     * @param name The name to check
     * @return true if a role exists with the name, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds all roles ordered by name.
     *
     * @return List of roles sorted by name
     */
    List<Role> findAllByOrderByNameAsc();

    /**
     * Counts the number of users assigned to a specific role.
     *
     * @param roleId The ID of the role
     * @return The number of users with the role
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersByRoleId(@Param("roleId") Long roleId);
}
