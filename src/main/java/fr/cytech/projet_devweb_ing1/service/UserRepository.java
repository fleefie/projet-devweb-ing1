package fr.cytech.projet_devweb_ing1.service;

import fr.cytech.projet_devweb_ing1.dto.UserSearchDTO;
import fr.cytech.projet_devweb_ing1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository managing users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(String nickname, String password);

    /**
     * Fetch all users of types found in the list passed.
     */
    @Query("SELECT u.id AS id, u.username AS username, u.type AS type FROM User u WHERE u.type IN :types")
    List<UserSearchDTO> findByTypeIn(@Param("types") List<User.UserType> types);

    /**
     * Search users by partial username, accepts blank inputs as all
     */
    @Query("SELECT u.id AS id, u.username AS username, u.type AS type FROM User u WHERE u.username LIKE %:keyword%")
    List<UserSearchDTO> searchByUsername(@Param("keyword") String keyword);
}
