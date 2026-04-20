package hu.bence.amusementpark.validation.validator;

import hu.bence.amusementpark.dto.request.VisitorSignUpRequestDto;
import hu.bence.amusementpark.validation.constraint.PasswordConfirmPasswordSameConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConfirmPasswordSameValidator
        implements ConstraintValidator<PasswordConfirmPasswordSameConstraint, VisitorSignUpRequestDto> {

    @Override
    public boolean isValid(VisitorSignUpRequestDto value, ConstraintValidatorContext context) {
        if (value.getPassword() == null || value.getConfirmPassword() == null) {
            return true;
        } else if (!value.getPassword().equals(value.getConfirmPassword())) {
            return false;
        }
        return true;
    }

}
