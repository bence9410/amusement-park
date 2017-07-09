package hu.beni.amusementpark.exception;

public class AmusementParkException extends RuntimeException {

    public AmusementParkException(String message) {
        super(message);
    }

    public AmusementParkException(String message, Throwable cause) {
        super(message, cause);
    }
}
