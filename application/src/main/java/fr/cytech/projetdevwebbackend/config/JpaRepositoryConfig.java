package fr.cytech.projetdevwebbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import fr.cytech.projetdevwebbackend.jpa.repository.JsonRepositoryFactoryBean;

@Configuration
@EnableJpaRepositories(basePackages = "fr.cytech.projetdevwebbackend", repositoryFactoryBeanClass = JsonRepositoryFactoryBean.class)
public class JpaRepositoryConfig {
}
