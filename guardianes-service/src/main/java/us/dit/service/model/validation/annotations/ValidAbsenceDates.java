package us.dit.service.model.validation.annotations;

import us.dit.service.model.validation.validators.AbsenceDatesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Uses the algorithm in {@link AbsenceDatesValidator}
 * 
 * @see AbsenceDatesValidator
 * 
 * @author miggoncan
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Constraint(validatedBy = { AbsenceDatesValidator.class })
public @interface ValidAbsenceDates {
	String message() default "{us.dit.service.model.entityvalidation.ValidAbsenceDates.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}