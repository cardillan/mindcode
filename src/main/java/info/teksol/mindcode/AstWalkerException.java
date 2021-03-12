package info.teksol.mindcode;

public class AstWalkerException extends RuntimeException {
    public AstWalkerException() {
    }

    public AstWalkerException(String message) {
        super(message);
    }

    public AstWalkerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AstWalkerException(Throwable cause) {
        super(cause);
    }

    public AstWalkerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
