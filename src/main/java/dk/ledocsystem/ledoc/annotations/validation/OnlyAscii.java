package dk.ledocsystem.ledoc.annotations.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;


/**
 * The annotated {@link CharSequence} must contain only ASCII characters.
 */
@Documented
@Constraint(validatedBy = {})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@ReportAsSingleViolation
@Pattern(regexp = "^\\p{ASCII}*$")
public @interface OnlyAscii {
    String message() default "The provided value contains non-ASCII characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

