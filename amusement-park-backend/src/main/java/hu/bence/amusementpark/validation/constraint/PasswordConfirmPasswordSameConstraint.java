package hu.bence.amusementpark.validation.constraint;

import hu.bence.amusementpark.validation.validator.PasswordConfirmPasswordSameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordConfirmPasswordSameValidator.class)
public @interface PasswordConfirmPasswordSameConstraint {

    String message() default "password and confirmPassword must be equals";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
