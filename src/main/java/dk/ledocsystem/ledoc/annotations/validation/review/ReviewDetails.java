package dk.ledocsystem.ledoc.annotations.validation.review;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated entity must contain all information required for review.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { ReviewDetailsValidatorForEmployee.class,
        ReviewDetailsValidatorForCreateEquipment.class,
        ReviewDetailsValidatorForEditEquipment.class})
public @interface ReviewDetails {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
