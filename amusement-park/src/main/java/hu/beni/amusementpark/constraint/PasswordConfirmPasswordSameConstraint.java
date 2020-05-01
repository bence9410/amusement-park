package hu.beni.amusementpark.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import hu.beni.amusementpark.validator.PasswordConfirmPasswordSameValidator;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordConfirmPasswordSameValidator.class)
public @interface PasswordConfirmPasswordSameConstraint {

	String message() default "password and confirmPassword must be equals";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
