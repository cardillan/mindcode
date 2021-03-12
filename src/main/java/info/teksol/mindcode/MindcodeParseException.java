package info.teksol.mindcode;

public class MindcodeParseException extends RuntimeException {
    public MindcodeParseException() {
    }

    public MindcodeParseException(String message) {
        super(message);
    }

    public MindcodeParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MindcodeParseException(Throwable cause) {
        super(cause);
    }

    public MindcodeParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
