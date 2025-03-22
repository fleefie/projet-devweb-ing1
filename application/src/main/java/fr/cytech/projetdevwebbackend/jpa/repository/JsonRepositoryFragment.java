package fr.cytech.projetdevwebbackend.jpa.repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository fragment interface providing JSON query capabilities for entities.
 * <p>
 * This interface defines methods for querying entities based on their JSON
 * field contents.
 * Spring Data repositories can extend this interface to gain JSON querying
 * capabilities.
 *
 * @param <T>  the domain type the repository manages
 * @param <ID> the type of the id of the entity the repository manages
 * 
 * @author fleefie
 * @since 2025-03-22
 */
public interface JsonRepositoryFragment<T, ID> {

    /**
     * Finds entities containing the specified text in any JSON field.
     *
     * @param searchText the text to search for
     * @return list of entities containing the search text
     */
    List<T> jsonSearchByValue(String searchText);

    /**
     * Finds entities that have the specified key in any JSON field.
     *
     * @param key the JSON key to search for
     * @return list of entities having the specified key
     */
    List<T> jsonSearchByKey(String key);

    /**
     * Finds entities where the specified key has the specified value in any JSON
     * field.
     *
     * @param key   the JSON key to search for
     * @param value the value the key should have
     * @return list of entities matching the key-value pair
     */
    List<T> jsonSearchByKeyAndValue(String key, Object value);

    /**
     * Finds the first entity where the specified key has the specified value.
     *
     * @param key   the JSON key to search for
     * @param value the value the key should have
     * @return an optional containing the first matching entity, or empty if none
     *         found
     */
    Optional<T> jsonSearchFirstByKeyAndValue(String key, Object value);

    /**
     * Checks if any entity has the specified key with the specified value.
     *
     * @param key   the JSON key to check
     * @param value the value the key should have
     * @return true if at least one entity matches, false otherwise
     */
    boolean existsByKeyAndValue(String key, Object value);

    /**
     * Counts entities that have the specified key in any JSON field.
     *
     * @param key the JSON key to count
     * @return the count of entities having the specified key
     */
    long countByKey(String key);
}
