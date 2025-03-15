package fr.cytech.projetdevwebbackend.auth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cytech.projetdevwebbackend.auth.dto.UsernameDto;
import fr.cytech.projetdevwebbackend.auth.service.UserAdministrationService;
import fr.cytech.projetdevwebbackend.auth.service.UserAdministrationService.UserAcceptationError;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserAdministrationController {

    @Autowired
    private UserAdministrationService userAdministrationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/acceptuser")
    public ResponseEntity<String> acceptUser(@RequestBody UsernameDto usernameDto) {
        if (usernameDto.getUsername().isBlank())
            return new ResponseEntity<>("{\"error\":\"UserNotFound\"}", HttpStatus.BAD_REQUEST);

        Optional<UserAcceptationError> acceptationStatus = userAdministrationService
                .acceptUser(usernameDto.getUsername());

        if (acceptationStatus.isEmpty())
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>("{\"error\":\"" + acceptationStatus.get().toString() + "\"}",
                    HttpStatus.BAD_REQUEST);
    }
}
