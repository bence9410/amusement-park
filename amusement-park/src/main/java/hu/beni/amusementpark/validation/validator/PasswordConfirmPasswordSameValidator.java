package hu.beni.amusementpark.validation.validator;

import hu.beni.amusementpark.dto.resource.VisitorResource;
import hu.beni.amusementpark.validation.constraint.PasswordConfirmPasswordSameConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordConfirmPasswordSameValidator
        implements ConstraintValidator<PasswordConfirmPasswordSameConstraint, VisitorResource> {

    @Override
    public boolean isValid(VisitorResource value, ConstraintValidatorContext context) {
        if (value.getPassword() == null || value.getConfirmPassword() == null) {
            return true;
        } else if (!value.getPassword().equals(value.getConfirmPassword())) {
            return false;
        }
        return true;
    }

}
