package hu.bence.amusementpark.exception;

import java.util.Optional;

public class ExceptionUtil {

    private ExceptionUtil() {
        super();
    }

    public static <T> T ifNull(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new AmusementParkException(message));
    }

    public static <T> T ifNotNull(T t, String message) {
        if (t != null) {
            throw new AmusementParkException(message);
        }
        return t;
    }

    public static void ifNotEquals(Object o1, Object o2, String message) {
        if (!o1.equals(o2)) {
            throw new AmusementParkException(message);
        }
    }

    public static void ifFirstLessThanSecond(long first, long second, String message) {
        if (first < second) {
            throw new AmusementParkException(message);
        }
    }

    public static void ifNotZero(long value, String message) {
        if (value != 0) {
            throw new AmusementParkException(message);
        }
    }

    public static void ifTrue(boolean value, String message) {
        if (value) {
            throw new AmusementParkException(message);
        }
    }

}
