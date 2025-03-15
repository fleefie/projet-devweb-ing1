package fr.cytech.projetdevwebbackend.auth.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cytech.projetdevwebbackend.auth.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
