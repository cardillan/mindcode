package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.GenerationException;

public class InsufficientArgumentsException extends GenerationException {
    public InsufficientArgumentsException() {
    }

    public InsufficientArgumentsException(String message) {
        super(message);
    }

    public InsufficientArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientArgumentsException(Throwable cause) {
        super(cause);
    }

    public InsufficientArgumentsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
