package fr.cytech.projetdevwebbackend.auth.service;

import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.cytech.projetdevwebbackend.auth.JwtTokenProvider;
import fr.cytech.projetdevwebbackend.auth.dto.LoginDto;
import fr.cytech.projetdevwebbackend.auth.model.User;
import fr.cytech.projetdevwebbackend.auth.model.repository.UserRepository;
import fr.cytech.projetdevwebbackend.util.Either;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(),
                loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return token;
    }

    public Boolean doesUserExists(String username, String email) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username, email);
        if (user.isPresent())
            return true;
        else
            return false;
    }

    public Either<UserAuthError, User> register(String username, String password, String email, String name) {
        if (!Pattern.compile(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
                .matcher(email)
                .matches())
            return Either.left(UserAuthError.EmailDoesNotMatchRegex);
        if (!StringUtils.isAlphanumeric(username))
            return Either.left(UserAuthError.UsernameNotAlphanum);
        if (doesUserExists(username, email))
            return Either.left(UserAuthError.UserExists);
        if (!StringUtils.isAlphanumericSpace(name))
            return Either.left(UserAuthError.NameNotAlphanumspace);
        if (!StringUtils.isAsciiPrintable(password))
            return Either.left(UserAuthError.PasswordNotAscii);
        if (StringUtils.isAlphanumericSpace(password))
            return Either.left(UserAuthError.PasswordOnlyAlphanum);
        if (password.length() < 15)
            return Either.left(UserAuthError.PasswordTooShort);
        if (password.isBlank())
            return Either.left(UserAuthError.EmptyPassword);
        if (username.isBlank())
            return Either.left(UserAuthError.EmptyUsername);
        if (name.isBlank())
            return Either.left(UserAuthError.EmptyName);

        return Either.right(userRepository.save(new User(name, username, email, passwordEncoder.encode(password))));
    }

    public enum UserAuthError {
        UserExists,
        UsernameNotAlphanum,
        NameNotAlphanumspace,
        EmailDoesNotMatchRegex,
        PasswordTooShort,
        PasswordOnlyAlphanum,
        PasswordNotAscii,
        PasswordsDoNotMatch,
        EmptyPassword,
        EmptyUsername,
        EmptyName
    }
}
