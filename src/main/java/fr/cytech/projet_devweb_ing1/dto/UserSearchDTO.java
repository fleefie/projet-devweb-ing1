package fr.cytech.projet_devweb_ing1.dto;

import fr.cytech.projet_devweb_ing1.model.User;

/**
 * Represents a user search. Excludes the password to project only non-sensitive
 * data to the querrier
 */
public interface UserSearchDTO {
    Integer getId();

    String getUsername();

    User.UserType getType();
}
