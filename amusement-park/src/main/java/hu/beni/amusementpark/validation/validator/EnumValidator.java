package hu.beni.amusementpark.validation.validator;

import hu.beni.amusementpark.validation.constraint.EnumConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidator implements ConstraintValidator<EnumConstraint, String> {

    private String message;

    private Set<String> validValues;

    @Override
    public void initialize(EnumConstraint constraint) {
        message = constraint.message();
        validValues = Stream.of(constraint.enumClazz().getEnumConstants()).map(Enum::toString)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (value == null) {
            context.buildConstraintViolationWithTemplate("must not be null").addConstraintViolation();
            return false;
        } else if (!validValues.contains(value)) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        } else {
            return true;
        }
    }

}
