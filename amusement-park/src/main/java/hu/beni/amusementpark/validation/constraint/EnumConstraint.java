package hu.beni.amusementpark.validation.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import hu.beni.amusementpark.validation.validator.EnumValidator;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface EnumConstraint {

	Class<? extends Enum<?>> enumClazz();

	String message() default "Not valid value.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
