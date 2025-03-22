package fr.cytech.projetdevwebbackend.jpa.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JsonRepository<T, ID extends Serializable> {
    /**
     * Finds entities where any JSON value matches the provided search text.
     *
     * @param searchText the text to search for in any JSON value
     * @return a list of entities that contain the search text in any of their JSON
     *         values
     */
    List<T> jsonSearchByValue(String searchText);

    /**
     * Finds entities that contain a specific JSON key.
     *
     * @param key the JSON key to search for
     * @return a list of entities that contain the specified JSON key
     */
    List<T> jsonSearchByKey(String key);

    /**
     * Finds entities where a specific JSON key matches a given value.
     *
     * @param key   the JSON key to match
     * @param value the value that the key should have
     * @return a list of entities where the specified key matches the given value
     */
    List<T> jsonSearchByKeyAndValue(String key, Object value);

    /**
     * Finds the first entity where a specific JSON key matches a given value.
     *
     * @param key   the JSON key to match
     * @param value the value that the key should have
     * @return an Optional containing the first matching entity, or an empty
     *         Optional if none found
     */
    Optional<T> jsonSearchFirstByKeyAndValue(String key, Object value);

    /**
     * Checks if any entity contains the specified JSON key with the given value.
     *
     * @param key   the JSON key to check
     * @param value the value to check for
     * @return true if at least one entity has the key with the given value, false
     *         otherwise
     */
    boolean existsByKeyAndValue(String key, Object value);

    /**
     * Counts entities that contain a specific JSON key.
     *
     * @param key the JSON key to search for
     * @return the number of entities that contain the specified JSON key
     */
    long countByKey(String key);
}
