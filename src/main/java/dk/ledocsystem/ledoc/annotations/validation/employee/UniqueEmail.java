package dk.ledocsystem.ledoc.annotations.validation.employee;


import dk.ledocsystem.ledoc.model.Employee;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The denoted target should be the unique email of {@link dk.ledocsystem.ledoc.model.Employee}.
 *
 * @see Employee#getEmail()
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { UniqueEmailValidatorForCharSequence.class })
public @interface UniqueEmail {

    String message() default "The provided email is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
