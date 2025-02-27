package fr.cytech.projet_devweb_ing1.util;

import jakarta.servlet.jsp.PageContext;

import java.util.Arrays;
import java.util.Optional;

/**
 * Utils related to JSP.
 */
public class JSPUtils {

    /**
     * Get an attribute either from the page context or the request.
     *
     * @param context the page context
     * @param name    the attribute name
     * @return the value corresponding to the given key if exists
     * @param <T> the type to automatically cast the value to
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getAttributeSingleKey(PageContext context, String name) {
        return Optional.ofNullable((T) context.getAttribute(name))
                .or(() -> Optional.ofNullable((T) context.getRequest().getAttribute(name)));
    }

    /**
     * Get an attribute whether from the page context or the request.
     *
     * @param context the page context
     * @param names   the attribute names, the second is a fallback for the first
     *                etc.
     * @return the value corresponding to the given key(s) if exists
     * @param <T> the type to automatically cast the value to
     */
    public static <T> Optional<T> getAttribute(PageContext context, String... names) {
        return Arrays.stream(names)
                .map(key -> JSPUtils.<T>getAttributeSingleKey(context, key))
                .reduce(
                        Optional.empty(),
                        (acc, err) -> acc.or(() -> err));
    }

    /**
     * Generate an error span if an attribute with the corresponding key(s)
     * (typically the error message) is present.
     *
     * @param context   the page context
     * @param errorKeys the attribute names, the second is a fallback for the first
     *                  etc.
     */
    public static void errorSpan(PageContext context, String... errorKeys) {
        getAttribute(context, errorKeys).ifPresent(msg -> {
            try {
                context.getOut().write("<span class=\"error\">" + msg + "</span>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Get the error class if the given error exists.
     *
     * @param context   the page context
     * @param errorKeys the attribute names, the second is a fallback for the first
     *                  etc.
     * @return "error_input" if the error exists
     */
    public static String errorInputClass(PageContext context, String... errorKeys) {
        return getAttribute(context, errorKeys).isPresent() ? "error_input" : "";
    }
}
