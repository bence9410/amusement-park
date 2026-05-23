package hu.bence.amusementpark.constants;

public class ErrorMessageConstants {

    public static final String ERROR = "Error:";
    public static final String UNEXPECTED_ERROR_OCCURRED = "Unexpected error occured!";
    public static final String COULD_NOT_GET_VALIDATION_MESSAGE = "Validation error occurred, but could not get error message.";
    public static final String COULD_NOT_FIND_USER = "Could not find user with name: %s.";
    public static final String NAME_ALREADY_TAKEN = "Name %s is already taken.";
    public static final String WRONG_COUPON_CODE = "Wrong coupon code.";
    public static final String ALREADY_ACTIVATED_COUPON_CODE = "Already activated coupon code.";
    public static final String NOT_VISITOR = "User %s is not a visitor!";
    public static final String AMUSEMENT_PARK_NOT_OWNED_BY_YOU = "The amusement park not owned by you!";
    public static final String NO_AMUSEMENT_PARK_WITH_ID = "No amusement park with the given id!";
    public static final String NO_MACHINE_IN_PARK_WITH_ID = "No machine in the park with the given id!";
    public static final String NO_USER_IN_PARK_WITH_ID = "No user in the park with the given id!";
    public static final String NO_USER_ON_MACHINE_WITH_ID = "No user on machine with the given id!";
    public static final String NOT_ENOUGH_MONEY = "Not enough money!";
    public static final String USER_IS_ON_A_MACHINE = "User is on a machine!";
    public static final String USER_IS_TOO_YOUNG = "User is too young!";
    public static final String USER_NOT_SIGNED_UP = "User not signed up!";
    public static final String USER_IS_IN_A_PARK = "User is in a park!";
    private static final String VALIDATION_ERROR_FIELD_MESSAGE = "Validation error: %s %s. ";
    private static final String VALIDATION_ERROR_MESSAGE = "Validation error: %s. ";

    private ErrorMessageConstants() {
        super();
    }

    public static String validationError(String field, String message) {
        return String.format(VALIDATION_ERROR_FIELD_MESSAGE, field, message);
    }

    public static String validationError(String message) {
        return String.format(VALIDATION_ERROR_MESSAGE, message);
    }
}
