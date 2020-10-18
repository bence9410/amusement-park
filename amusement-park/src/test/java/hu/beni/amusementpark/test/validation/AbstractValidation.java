package hu.beni.amusementpark.test.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public abstract class AbstractValidation {

	static {
		Locale.setDefault(Locale.ENGLISH);
	}

	private final Validator validator;

	protected AbstractValidation() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	protected void validateAndAssertNoViolations(Object object) {
		assertTrue(validator.validate(object).isEmpty());
	}

	protected void validateAndAssertViolationsSizeIsOneAndViolationIs(Object object, Object invalidValue,
			String propertyName, String message) {
		Set<ConstraintViolation<Object>> violations = validator.validate(object);
		assertEquals(violations.toString(), 1, violations.size());

		ConstraintViolation<Object> violation = violations.iterator().next();
		assertEquals(invalidValue, violation.getInvalidValue());
		assertEquals(propertyName, violation.getPropertyPath().toString());
		assertEquals(message, violation.getMessage());
	}

	protected void validateAndAssertViolationsSizeIsTwoAndFieldViolationIs(Object object, Object invalidValue,
			String propertyName, String message) {
		Set<ConstraintViolation<Object>> violations = validator.validate(object);
		assertEquals(violations.toString(), 2, violations.size());

		ConstraintViolation<Object> violation = violations.stream()
				.filter(v -> !v.getPropertyPath().toString().equals("")).findFirst().get();

		assertEquals(invalidValue, violation.getInvalidValue());
		assertEquals(propertyName, violation.getPropertyPath().toString());
		assertEquals(message, violation.getMessage());
	}

}
