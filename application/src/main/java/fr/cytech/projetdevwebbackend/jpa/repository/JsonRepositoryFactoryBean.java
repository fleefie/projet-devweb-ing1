package fr.cytech.projetdevwebbackend.jpa.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import jakarta.persistence.EntityManager;

/**
 * Factory bean for creating custom JSON repository instances.
 * <p>
 * This factory bean enables Spring Data JPA to create repositories that extend
 * JsonRepositoryFragment with the appropriate implementation. It integrates
 * with
 * Spring's repository creation mechanism to provide JSON query capabilities.
 *
 * @param <R>  the repository type
 * @param <T>  the entity type
 * @param <ID> the entity ID type
 * 
 * @author fleefie
 * @since 2025-03-22
 */
public class JsonRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, ID> {

    /**
     * Creates a new JsonRepositoryFactoryBean with the given repository interface.
     *
     * @param repositoryInterface the repository interface
     */
    public JsonRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new JsonRepositoryFactory<>(entityManager);
    }

    /**
     * Custom repository factory that creates JsonRepositoryImpl instances when
     * appropriate.
     *
     * @param <T>  the entity type
     * @param <ID> the entity ID type
     */
    private static class JsonRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {
        private final EntityManager entityManager;

        /**
         * Creates a new JsonRepositoryFactory with the given EntityManager.
         *
         * @param entityManager the JPA EntityManager
         */
        public JsonRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                EntityManager entityManager) {
            // Check if the repository extends JsonRepository
            if (JsonRepositoryFragment.class.isAssignableFrom(information.getRepositoryInterface())) {
                return new JsonRepositoryImpl<>(
                        getEntityInformation(information.getDomainType()),
                        entityManager);
            }

            return super.getTargetRepository(information, entityManager);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            // Select JsonRepositoryImpl as the base class for repositories that
            // extend JsonRepositoryFragment
            if (JsonRepositoryFragment.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                return JsonRepositoryImpl.class;
            }

            return super.getRepositoryBaseClass(metadata);
        }
    }
}
