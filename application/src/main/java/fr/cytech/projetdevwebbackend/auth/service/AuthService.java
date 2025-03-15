package fr.cytech.projetdevwebbackend.auth.service;

import fr.cytech.projetdevwebbackend.auth.dto.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);
}
