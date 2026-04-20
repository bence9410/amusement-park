package hu.bence.amusementpark.constants;

public class ValidationMessageConstants {

    private static final String SIZE_MESSAGE = "size must be between %d and %d";
    private static final String RANGE_MESSAGE = "must be between %d and %d";

    private ValidationMessageConstants() {
        super();
    }

    public static String sizeMessage(long min, long max) {
        return String.format(SIZE_MESSAGE, min, max);
    }

    public static String rangeMessage(long min, long max) {
        return String.format(RANGE_MESSAGE, min, max);
    }

}
