package fr.cytech.projet_devweb_ing1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving the user search page.
 */
@Controller
public class UserSearchController {

    @GetMapping("/users")
    public String showSearchPage() {
        return "users/search";
    }
}
