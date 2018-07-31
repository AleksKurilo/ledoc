package dk.ledocsystem.ledoc.annotations.validation.employee;

import dk.ledocsystem.ledoc.model.employee.Employee;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The denoted target should be the unique email of {@link dk.ledocsystem.ledoc.model.employee.Employee}.
 *
 * @see Employee#getUsername() ()
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { UniqueUsernameValidator.class })
public @interface UniqueUsername {

    String message() default "The provided username is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
