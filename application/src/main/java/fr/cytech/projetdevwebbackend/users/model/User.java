package fr.cytech.projetdevwebbackend.users.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user account in the system.
 * <p>
 * Users can have multiple roles and various status flags including verification
 * status.
 * This entity handles core user information for authentication and
 * authorization, as well as user score.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(exclude = { "password", "roles" })
@EqualsAndHashCode(of = { "id", "username", "email" })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NonNull
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "Username must be alphanumeric")
    private String username;

    @NonNull
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NonNull
    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    private String password;

    @NonNull
    @Column(nullable = false)
    private Boolean verified;

    @NonNull
    @Column(nullable = false)
    private Boolean locked = false;

    @NonNull
    @Column(nullable = false)
    private Boolean enabled = true;

    @NonNull
    @Column(nullable = false)
    private Integer score = 0;

    @Column(nullable = false)
    private String gender;

    @NonNull
    @Column(nullable = false)
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Birthdate must be in the format 'YYYY-MM-DD'")
    private String birthdate;

    @NonNull
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    /**
     * Reports made by this user against other users.
     */
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reportsMade = new HashSet<>();

    /**
     * Reports received by this user from other users.
     */
    @OneToMany(mappedBy = "reported", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reportsReceived = new HashSet<>();

    /**
     * Reports another user with a specific reason.
     *
     * @param targetUser The user to report
     * @param reason     The reason for reporting the user
     * @return true if the report was created, false if already reported or invalid
     */
    public boolean reportUser(User targetUser, String reason) {
        if (targetUser.equals(this) || reason == null || reason.trim().isEmpty()) {
            return false; // Cannot report self or without valid reason
        }

        // Check if already reported
        boolean alreadyReported = reportsMade.stream()
                .anyMatch(report -> report.getReported().equals(targetUser));

        if (alreadyReported) {
            return false;
        }

        Report report = new Report(this, targetUser, reason);
        return reportsMade.add(report);
    }

    /**
     * Default constructor required by JPA.
     */
    protected User() {
        this.roles = new HashSet<>();
    }

    /**
     * Creates a new user with the specified details.
     *
     * @param name     User's full name
     * @param username User's unique username
     * @param email    User's email address
     * @param password User's hashed password
     * @param verified Whether the user's email is verified
     */
    public User(@NonNull String name, @NonNull String username, @NonNull String email,
            @NonNull String password, @NonNull Boolean verified) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.roles = new HashSet<>();
        this.locked = false;
        this.enabled = true;
    }

    /**
     * Change user score additively.
     *
     * @param delta score difference to apply
     */
    public void addScore(Integer delta) {
        this.score += delta;
    }

    /**
     * Adds a role to this user.
     *
     * @param role The role to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Removes a role from this user.
     *
     * @param role The role to remove
     * @return true if the role was removed, false if it wasn't present
     */
    public boolean removeRole(Role role) {
        return this.roles.remove(role);
    }

    /**
     * Checks if this user has a specific role by name.
     *
     * @param roleName The name of the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    /**
     * Checks if this user's account is locked.
     *
     * @return true if the account is locked, false otherwise
     */
    public boolean isLocked() {
        return locked != null && locked;
    }

    /**
     * Checks if this user's account is enabled.
     *
     * @return true if the account is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    /**
     * Checks if this user has a verified email.
     * 
     * @return true if the email is verified, false otherwise
     */
    public boolean isVerified() {
        return verified != null && verified;
    }

    /**
     * Checks if this user's account has been accepted (doesn't have PENDING role).
     *
     * @return true if the account is accepted, false if it still has PENDING role
     */
    public boolean isAccepted() {
        return !hasRole("PENDING");
    }
}
