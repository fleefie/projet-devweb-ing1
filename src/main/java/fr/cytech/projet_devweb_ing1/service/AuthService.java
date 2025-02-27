package fr.cytech.projet_devweb_ing1.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import fr.cytech.projet_devweb_ing1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Optional;

/**
 * A service dedicated to authentication.
 */
@Service
public class AuthService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * Check if a user with the given username exists.
     *
     * @param username the name of the user to check
     * @return `true` if a user with this name exists
     */
    public boolean doesUserExist(String username) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", StringEscapeUtils.escapeHtml4(username));
        return !query.getResultList().isEmpty();
    }

    /**
     * Try to log in the user using its name and password.
     *
     * @param username the name of the user
     * @param password the password of the user
     * @return the User corresponding to the given credentials or an empty Optional
     *         if no user is matched
     */
    public Optional<User> login(String username, String password) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            User user = query.getSingleResult();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Register a new user.
     *
     * @param username the name of the user
     * @param password the password of the user
     * @return a new user with the given credentials
     */
    public User register(String username, String password) {
        String sanitizedUsername = StringEscapeUtils.escapeHtml4(username);
        String hashedPassword = passwordEncoder.encode(StringEscapeUtils.escapeHtml4(password));
        return userRepository.save(
                new User(sanitizedUsername, hashedPassword, User.UserType.SIMPLE));
    }
}
