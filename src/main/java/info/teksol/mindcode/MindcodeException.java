package info.teksol.mindcode;

public class MindcodeException extends RuntimeException {
    public MindcodeException() {
    }

    public MindcodeException(String message) {
        super(message);
    }

    public MindcodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MindcodeException(Throwable cause) {
        super(cause);
    }

    public MindcodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
