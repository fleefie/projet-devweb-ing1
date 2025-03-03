package fr.cytech.projet_devweb_ing1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import fr.cytech.projet_devweb_ing1.model.User;
import fr.cytech.projet_devweb_ing1.service.AuthService;
import jakarta.servlet.http.HttpSession;

@Controller
public class IndexController implements WebMvcConfigurer {
    @Autowired
    AuthService authService;

    /**
     * Website index.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null)
            return "index";
        return "redirect:/auth";
    }
}
