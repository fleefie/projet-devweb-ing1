package fr.cytech.projet_devweb_ing1.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Utils for validation.
 */
public class ValidationUtils {

    /**
     * Build a ModelAndView from the given binding result.
     *
     * @param view          the view to return
     * @param bindingResult the result to fill the attributes
     * @return the ModelAndView built from the binding result
     */
    public static ModelAndView errorsAndView(String view, BindingResult bindingResult) {
        Map<String, String> model = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            model.put("error_" + fieldError.getField(), fieldError.getDefaultMessage());
        }

        if (bindingResult.getGlobalError() != null)
            model.put("error", bindingResult.getGlobalError().getDefaultMessage());

        return new ModelAndView(view, model);
    }
}
