package fr.cytech.projetdevwebbackend.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.cytech.projetdevwebbackend.auth.model.Role;
import fr.cytech.projetdevwebbackend.auth.model.User;
import fr.cytech.projetdevwebbackend.auth.model.repository.RoleRepository;
import fr.cytech.projetdevwebbackend.auth.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserAdministrationService {

    private RoleRepository roleRepository;
    private UserRepository userRepository;

    @Transactional
    public Optional<UserAcceptationError> acceptUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Optional<Role> rolePending = roleRepository.findByName("PENDING");
        Optional<Role> roleUser = roleRepository.findByName("USER");

        if (user.isEmpty())
            return Optional.of(UserAcceptationError.UserNotFound);
        if (rolePending.isEmpty())
            return Optional.of(UserAcceptationError.PendingRoleNotFound);
        if (roleUser.isEmpty())
            return Optional.of(UserAcceptationError.UserRoleNotFound);
        if (!user.get().getRoles().contains(rolePending.get()))
            return Optional.of(UserAcceptationError.UserAlreadyVerified);

        user.get().removeRole(rolePending.get());
        user.get().addRole(roleUser.get());

        userRepository.save(user.get());

        return Optional.empty();
    }

    public enum UserAcceptationError {
        UserNotFound,
        PendingRoleNotFound,
        UserRoleNotFound,
        UserAlreadyVerified
    }
}
