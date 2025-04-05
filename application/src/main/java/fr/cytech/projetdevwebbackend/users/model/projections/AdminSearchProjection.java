package fr.cytech.projetdevwebbackend.users.model.projections;

import java.util.Set;

import fr.cytech.projetdevwebbackend.users.model.Role;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface AdminSearchProjection {
    String getUsername();

    String getName();

    String getEmail();

    Integer getScore();

    String getGender();

    String getBirthdate();

    @Value("#{target.enabled}")
    Boolean isEnabled();

    @Value("#{target.locked}")
    Boolean isLocked();

    @Value("#{target.verified}")
    Boolean isVerified();

    @JsonIgnore
    Set<Role> getRoles();

    default List<String> getRoleNames() {
        return getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
