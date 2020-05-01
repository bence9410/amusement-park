package hu.beni.amusementpark.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import hu.beni.amusementpark.constraint.PasswordConfirmPasswordSameConstraint;
import hu.beni.amusementpark.dto.resource.VisitorResource;

public class PasswordConfirmPasswordSameValidator
		implements ConstraintValidator<PasswordConfirmPasswordSameConstraint, VisitorResource> {

	@Override
	public boolean isValid(VisitorResource value, ConstraintValidatorContext context) {
		return value.getPassword().equals(value.getConfirmPassword());
	}

}
