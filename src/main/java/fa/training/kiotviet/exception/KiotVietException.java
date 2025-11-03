package fa.training.kiotviet.exception;

/**
 * Base exception class for all KiotViet application exceptions.
 */
public class KiotVietException extends RuntimeException {

    private final String errorCode;

    public KiotVietException(String message) {
        super(message);
        this.errorCode = "GENERAL_ERROR";
    }

    public KiotVietException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GENERAL_ERROR";
    }

    public KiotVietException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public KiotVietException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}