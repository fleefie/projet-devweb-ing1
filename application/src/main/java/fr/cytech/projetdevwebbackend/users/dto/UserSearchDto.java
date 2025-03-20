package fr.cytech.projetdevwebbackend.users.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserSearchDto {
    private String username;
    private List<String> roles;
}
