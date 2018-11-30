package dk.ledocsystem.service.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * The annotated {@link CharSequence} must be a valid password.
 */
@Documented
@Constraint(validatedBy = {})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@ReportAsSingleViolation
@Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Za-z])([@#$%^&+=]?)(?=\\S+$).{5,40}$")
public @interface Password {
    String message() default "Password needs to be from 5 to 40 characters long; consist only from letters, digits and special characters; include at least one letter and one digit.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

