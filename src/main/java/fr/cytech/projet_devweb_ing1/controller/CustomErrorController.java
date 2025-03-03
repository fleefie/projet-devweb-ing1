package fr.cytech.projet_devweb_ing1.controller;

import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (status != null) {
            return new ModelAndView("error",
                    Map.of("error",
                            status.toString() + ": " + (message != null ? message.toString() : "Unknown Error"),
                            "error_code", status.toString()));
        }

        return new ModelAndView("redirect:/");
    }
}
