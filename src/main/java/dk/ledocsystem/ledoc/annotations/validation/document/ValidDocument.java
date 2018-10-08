package dk.ledocsystem.ledoc.annotations.validation.document;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {DocumentValidator.class})
public @interface ValidDocument {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
