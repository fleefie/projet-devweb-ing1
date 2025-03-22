package fr.cytech.projetdevwebbackend.jpa.repository;

import java.util.List;
import java.util.Optional;

public interface JsonRepositoryFragment<T, ID> {
    List<T> findByValues(String searchText);

    List<T> findByKey(String key);

    List<T> findByKeyAndValue(String key, Object value);

    Optional<T> findFirstByKeyAndValue(String key, Object value);

    boolean existsByKeyAndValue(String key, Object value);

    long countByKey(String key);
}
