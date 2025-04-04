package fr.cytech.projetdevwebbackend.users.model.projections;

import java.util.Set;

import fr.cytech.projetdevwebbackend.users.model.Role;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface UserSearchProjection {
    String getUsername();

    Integer getPoints();

    String getGender();

    String getBirthdate();

    @JsonIgnore
    Set<Role> getRoles();

    default List<String> getRoleNames() {
        return getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
