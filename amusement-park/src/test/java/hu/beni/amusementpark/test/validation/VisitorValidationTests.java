package hu.beni.amusementpark.test.validation;

import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_16_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_21_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_26_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_4_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_59_LENGTH;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.EMAIL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NULL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.PAST_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.rangeMessage;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.sizeMessage;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.dto.resource.VisitorResource;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.ValidResourceFactory;

public class VisitorValidationTests extends AbstractValidation {

	private static final String EMAIL = "email";
	private static final String PASSWORD = "password";
	private static final String CONFIRM_PASSWORD = "confirmPassword";
	private static final String AUTHORITY = "authority";
	private static final String DATE_OF_BIRTH = "dateOfBirth";
	private static final String SPENDING_MONEY = "spendingMoney";

	private Visitor visitor;

	private VisitorResource visitorResource;

	@Before
	public void setUp() {
		visitor = createVisitor();

		visitorResource = ValidResourceFactory.createVisitor();
	}

	@Test
	public void validVisitor() {
		validateAndAssertNoViolations(visitor);
	}

	@Test
	public void invalidEmail() {
		String email = null;

		visitor.setEmail(email);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getEmail(), EMAIL, NOT_NULL_MESSAGE);

		visitorResource.setEmail(email);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitorResource, visitorResource.getEmail(), EMAIL,
				NOT_NULL_MESSAGE);

		email = STRING_WITH_4_LENGTH;

		visitor.setEmail(email);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getEmail(), EMAIL, EMAIL_MESSAGE);

		visitorResource.setEmail(email);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitorResource, visitorResource.getEmail(), EMAIL,
				EMAIL_MESSAGE);
	}

	@Test
	public void invalidPassword() {
		visitor.setPassword(null);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getPassword(), PASSWORD, NOT_NULL_MESSAGE);

		visitorResource.setPassword(null);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitorResource, visitorResource.getPassword(), PASSWORD,
				NOT_NULL_MESSAGE);

		visitor.setPassword(STRING_WITH_59_LENGTH);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getPassword(), PASSWORD,
				sizeMessage(60, 60));

		visitor.setPassword(STRING_WITH_59_LENGTH);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getPassword(), PASSWORD,
				sizeMessage(60, 60));

		visitorResource.setPassword(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsTwoAndFieldViolationIs(visitorResource, visitorResource.getPassword(),
				PASSWORD, sizeMessage(5, 25));

		visitorResource.setPassword(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsTwoAndFieldViolationIs(visitorResource, visitorResource.getPassword(),
				PASSWORD, sizeMessage(5, 25));

		visitorResource.setPassword(STRING_WITH_16_LENGTH);
		visitorResource.setConfirmPassword(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsTwoAndFieldViolationIs(visitorResource, visitorResource.getConfirmPassword(),
				CONFIRM_PASSWORD, sizeMessage(5, 25));

		visitorResource.setConfirmPassword(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsTwoAndFieldViolationIs(visitorResource, visitorResource.getConfirmPassword(),
				CONFIRM_PASSWORD, sizeMessage(5, 25));

		visitorResource.setConfirmPassword(STRING_WITH_21_LENGTH);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitorResource, visitorResource, "",
				"password and confirmPassword must be equals");
	}

	@Test
	public void invalidAuthority() {
		visitor.setAuthority(null);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getAuthority(), AUTHORITY,
				NOT_NULL_MESSAGE);

		visitorResource.setAuthority("ROLE_VISITOR");
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitorResource, visitorResource.getAuthority(), AUTHORITY,
				NULL_MESSAGE);

		visitor.setAuthority(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getAuthority(), AUTHORITY,
				sizeMessage(5, 25));

		visitor.setAuthority(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getAuthority(), AUTHORITY,
				sizeMessage(5, 25));
	}

	@Test
	public void invalidDateOfBirth() {
		LocalDate dateOfBirth = null;

		visitor.setDateOfBirth(dateOfBirth);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getDateOfBirth(), DATE_OF_BIRTH,
				NOT_NULL_MESSAGE);

		visitorResource.setDateOfBirth(dateOfBirth);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitorResource, visitorResource.getDateOfBirth(),
				DATE_OF_BIRTH, NOT_NULL_MESSAGE);

		dateOfBirth = LocalDate.now();

		visitor.setDateOfBirth(dateOfBirth);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getDateOfBirth(), DATE_OF_BIRTH,
				PAST_MESSAGE);

		visitorResource.setDateOfBirth(dateOfBirth);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitorResource, visitorResource.getDateOfBirth(),
				DATE_OF_BIRTH, PAST_MESSAGE);
	}

	@Test
	public void invalidSpendingMoney() {
		visitor.setSpendingMoney(null);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getSpendingMoney(), SPENDING_MONEY,
				NOT_NULL_MESSAGE);

		visitorResource.setSpendingMoney(250);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitorResource, visitorResource.getSpendingMoney(),
				SPENDING_MONEY, NULL_MESSAGE);

		visitor.setSpendingMoney(-1);
		validateAndAssertViolationsSizeIsOneAndViolationIs(visitor, visitor.getSpendingMoney(), SPENDING_MONEY,
				rangeMessage(0, Integer.MAX_VALUE));
	}
}
