package fr.cytech.projet_devweb_ing1.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import fr.cytech.projet_devweb_ing1.dto.LoginDTO;
import fr.cytech.projet_devweb_ing1.dto.RegisterDTO;
import fr.cytech.projet_devweb_ing1.model.User;
import fr.cytech.projet_devweb_ing1.service.AuthService;
import fr.cytech.projet_devweb_ing1.util.ValidationUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Controller for authentication-related endpoints.
 */
@Controller
public class AuthController implements WebMvcConfigurer {

    @Autowired
    private AuthService authService;

    /**
     * Auth page.
     */
    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public String loginPage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null)
            return "redirect:/";
        else
            return "auth";
    }

    /**
     * Login auth endpoint.
     */
    @RequestMapping(value = "/auth/endpoint/login", method = RequestMethod.POST)
    public ModelAndView login(@Valid LoginDTO dto, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors())
            return ValidationUtils.errorsAndView("auth", bindingResult);
        Optional<User> user = authService.login(dto.getLoginUsername(), dto.getLoginPassword());
        if (user.isPresent()) {
            session.setAttribute("user", user.get());
            return new ModelAndView("redirect:/");
        } else {
            return new ModelAndView("redirect:/auth", Map.of("error_login", "Invalid creditentials"));
        }
    }

    /**
     * Register auth endpoint.
     */
    @RequestMapping(value = "/auth/endpoint/register", method = RequestMethod.POST)
    public ModelAndView register(@Valid RegisterDTO dto, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors())
            return ValidationUtils.errorsAndView("auth", bindingResult);
        Map<String, String> errors = new HashMap<>();
        if (!dto.isPasswordConfirmed())
            errors.put("error_registerPassword", "Passwords do not match");
        if (authService.doesUserExist(dto.getRegisterUsername()))
            errors.put("error_registerUsername", "Username already in use");
        if (errors.isEmpty()) {
            session.setAttribute("user", authService.register(dto.getRegisterUsername(), dto.getRegisterPassword()));
            return new ModelAndView("redirect:/");
        } else
            return new ModelAndView("redirect:/auth", errors);
    }
}
