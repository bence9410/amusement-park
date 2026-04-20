package hu.bence.amusementpark.constants;

public class ErrorMessageConstants {

    public static final String ERROR = "Error:";
    public static final String UNEXPECTED_ERROR_OCCURRED = "Unexpected error occured!";
    public static final String COULD_NOT_GET_VALIDATION_MESSAGE = "Validation error occurred, but could not get error message.";
    public static final String COULD_NOT_FIND_USER = "Could not find user with email: %s.";
    public static final String EMAIL_ALREADY_TAKEN = "Email %s is already taken.";
    public static final String AMUSEMENT_PARK_NOT_OWNED_BY_YOU = "The amusement park not owned by you!";
    public static final String NO_AMUSEMENT_PARK_WITH_ID = "No amusement park with the given id!";
    public static final String NO_MACHINE_IN_PARK_WITH_ID = "No machine in the park with the given id!";
    public static final String NO_USER_IN_PARK_WITH_ID = "No user in the park with the given id!";
    public static final String NO_USER_ON_MACHINE_WITH_ID = "No user on machine with the given id!";
    public static final String NO_GUEST_BOOK_REGISTRY_WITH_ID = "No guest book registry with the given id!";
    public static final String MACHINE_IS_TOO_EXPENSIVE = "Machine is too expensive!";
    public static final String MACHINE_IS_TOO_BIG = "Machine is too big!";
    public static final String USERS_ON_MACHINE = "Users on machine!";
    public static final String NOT_ENOUGH_MONEY = "Not enough money!";
    public static final String USER_IS_ON_A_MACHINE = "User is on a machine!";
    public static final String USER_IS_TOO_YOUNG = "User is too young!";
    public static final String NO_FREE_SEAT_ON_MACHINE = "No free seat on machine!";
    public static final String USER_NOT_SIGNED_UP = "User not signed up!";
    public static final String USER_IS_IN_A_PARK = "User is in a park!";
    public static final String USERS_IN_PARK = "Users in the park!";
    public static final String CAN_NOT_DELETE_ADMIN = "Can not delete admin!";
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
