package fr.cytech.projetdevwebbackend.jpa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Annotation to mark a field as containing JSON data for use with
 * JsonRepository.
 * <p>
 * Fields annotated with @JsonColumn are identified by JsonRepositoryImpl for
 * performing JSON-based queries. The annotated field must be of type String
 * and should contain valid JSON content.
 * 
 * @author fleefie
 * @since 2025-03-22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonColumn {
}
