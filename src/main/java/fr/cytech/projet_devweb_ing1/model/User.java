package fr.cytech.projet_devweb_ing1.model;

import jakarta.persistence.*;

/**
 * A user
 */
@Entity
@Table(name = "users")
public class User {
    public enum UserType {
        SIMPLE,
        COMPLEX,
        ADMIN,
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private UserType type;

    protected User() {
    }

    /**
     * Create a new user.
     *
     * @param username the name of this user
     * @param password the password of this user
     * @param type     the type of this user
     */
    public User(String username, String password, UserType type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public String getPassword() {
        return this.password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserType getType() {
        return type;
    }

}
