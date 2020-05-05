package hu.beni.amusementpark.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import hu.beni.amusementpark.constraint.PasswordConfirmPasswordSameConstraint;
import hu.beni.amusementpark.dto.resource.VisitorResource;

public class PasswordConfirmPasswordSameValidator
		implements ConstraintValidator<PasswordConfirmPasswordSameConstraint, VisitorResource> {

	private String message;

	@Override
	public void initialize(PasswordConfirmPasswordSameConstraint constraint) {
		message = constraint.message();
	}

	@Override
	public boolean isValid(VisitorResource value, ConstraintValidatorContext context) {
		// context.disableDefaultConstraintViolation();
		if (value.getPassword() == null || value.getConfirmPassword() == null) {
			return true;
		} else if (!value.getPassword().equals(value.getConfirmPassword())) {

			// context.buildConstraintViolationWithTemplate(message).addPropertyNode("password").addConstraintViolation();
			return false;
		}
		return true;
	}

}
