package info.teksol.mindcode;

public class GenerationException extends MindcodeException {
    public GenerationException() {
    }

    public GenerationException(String message) {
        super(message);
    }

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerationException(Throwable cause) {
        super(cause);
    }

    public GenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
