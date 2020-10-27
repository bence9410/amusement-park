package hu.beni.amusementpark.constants;

import java.util.stream.IntStream;

public class StringParamConstants {

	public static final String OPINION_ON_THE_PARK = "Amazing";

	public static final String STRING_EMPTY = "";
	public static final String STRING_WITH_1_LENGTH = "q";
	public static final String STRING_WITH_2_LENGTH = "qw";
	public static final String STRING_WITH_4_LENGTH = STRING_WITH_2_LENGTH + "er";
	public static final String STRING_WITH_6_LENGTH = STRING_WITH_4_LENGTH + "tz";
	public static final String STRING_WITH_11_LENGTH = STRING_WITH_6_LENGTH + "uiopa";
	public static final String STRING_WITH_16_LENGTH = STRING_WITH_11_LENGTH + "sdfgh";
	public static final String STRING_WITH_21_LENGTH = STRING_WITH_16_LENGTH + "jklyx";
	public static final String STRING_WITH_26_LENGTH = STRING_WITH_21_LENGTH + "cvbnm";
	public static final String STRING_WITH_59_LENGTH = STRING_WITH_26_LENGTH + STRING_WITH_26_LENGTH
			+ STRING_WITH_6_LENGTH + "+";
	public static final String STRING_WITH_61_LENGTH = STRING_WITH_59_LENGTH + STRING_WITH_2_LENGTH;
	public static final String STRING_WITH_101_LENGTH;

	public static final String VALID_PASSWORD = "Pass1234";
	public static final String PASSWORD_TOO_SHORT = "Pa2";
	public static final String PASSWORD_TOO_LONG = PASSWORD_TOO_SHORT + STRING_WITH_26_LENGTH;
	public static final String PASSWORD_NO_LOWERCASE = "PASSWORD2";
	public static final String PASSWORD_NO_UPPERCASE = "password2";
	public static final String PASSWORD_NO_NUMBER = "Password";
	public static final String PASSWORD_WITH_SPECIAL_CHARACTER = VALID_PASSWORD + "!";

	static {
		StringBuilder sb = new StringBuilder(STRING_WITH_26_LENGTH);
		IntStream.range(0, 3).forEach(i -> sb.append(STRING_WITH_21_LENGTH));
		sb.append(STRING_WITH_6_LENGTH);
		sb.append(STRING_WITH_4_LENGTH);
		sb.append(STRING_WITH_2_LENGTH);
		STRING_WITH_101_LENGTH = sb.toString();
	}

}
