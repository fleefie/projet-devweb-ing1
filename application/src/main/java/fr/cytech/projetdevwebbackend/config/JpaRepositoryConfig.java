package fr.cytech.projetdevwebbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import fr.cytech.projetdevwebbackend.jpa.repository.JsonRepositoryFactoryBean;

/**
 * Configuration class to enable custom JSON repository functionality.
 * <p>
 * This configuration registers the JsonRepositoryFactoryBean with Spring Data
 * JPA
 * to enable JSON query capabilities for repository interfaces that extend
 * JsonRepositoryFragment.
 * 
 * @author fleefie
 * @since 2025-03-22
 */
@Configuration
@EnableJpaRepositories(basePackages = "fr.cytech.projetdevwebbackend", repositoryFactoryBeanClass = JsonRepositoryFactoryBean.class)
public class JpaRepositoryConfig {
    // No additional configuration needed
}
