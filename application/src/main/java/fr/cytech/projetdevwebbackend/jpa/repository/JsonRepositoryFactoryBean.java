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

public class JsonRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, ID> {

    public JsonRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new JsonRepositoryFactory<>(entityManager);
    }

    private static class JsonRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {
        private final EntityManager entityManager;

        public JsonRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                EntityManager entityManager) {
            // Check if the repository extends JsonRepository
            if (JsonRepository.class.isAssignableFrom(information.getRepositoryInterface())) {
                return new JsonRepositoryImpl<>(
                        getEntityInformation(information.getDomainType()),
                        entityManager);
            }

            return super.getTargetRepository(information, entityManager);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            // Check if the repository extends JsonRepository
            if (JsonRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                return JsonRepositoryImpl.class;
            }

            return super.getRepositoryBaseClass(metadata);
        }
    }
}
