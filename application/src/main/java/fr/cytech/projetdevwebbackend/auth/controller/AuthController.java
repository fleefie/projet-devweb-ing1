package fr.cytech.projetdevwebbackend.auth.controller;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.cytech.projetdevwebbackend.auth.JwtAuthResponse;
import fr.cytech.projetdevwebbackend.auth.dto.LoginDto;
import fr.cytech.projetdevwebbackend.auth.dto.RegisterDto;
import fr.cytech.projetdevwebbackend.auth.model.User;
import fr.cytech.projetdevwebbackend.auth.service.AuthServiceImpl;
import fr.cytech.projetdevwebbackend.auth.service.AuthServiceImpl.UserAuthError;
import fr.cytech.projetdevwebbackend.util.Either;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    // Build Login REST API
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (!registerDto.getPassword().equals(registerDto.getPasswordConfirm())) {
            return new ResponseEntity<String>("{\"error\":\"PasswordsDoNotMatch\"}",
                    HttpStatus.BAD_REQUEST);
        }

        Either<UserAuthError, User> user = authService.register(
                registerDto.getUsername(),
                registerDto.getPassword(),
                registerDto.getEmail(),
                registerDto.getName(),
                true);

        if (user.isLeft())
            return new ResponseEntity<String>("{\"error\":\"" + user.getLeft().toString() + "\"}",
                    HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<String>("{}", HttpStatus.OK);
    }
}
