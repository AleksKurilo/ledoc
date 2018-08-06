package dk.ledocsystem.ledoc.annotations.validation.trade;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { UniqueTradeNameValidator.class })
public @interface UniqueTradeName {

    String message() default "The provided trade name is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
