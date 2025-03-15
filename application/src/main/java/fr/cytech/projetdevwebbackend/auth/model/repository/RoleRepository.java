package fr.cytech.projetdevwebbackend.auth.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cytech.projetdevwebbackend.auth.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
